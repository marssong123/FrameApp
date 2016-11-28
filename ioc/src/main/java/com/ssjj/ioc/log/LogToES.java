package com.ssjj.ioc.log;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import com.ssjj.ioc.utils.AdaUtils;
import com.ssjj.ioc.utils.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by GZ1581 on 2016/5/13
 */

public final class LogToES {

    // can be overwrite in ada value
    public static AtomicBoolean StoreExist = new AtomicBoolean(false);
    public static File RootDir;
    public static String LogPath = "/ada/logs";

    private static final String LogName = "logs.txt";
    private static final String UnCaughtException = "uncaught_exception.txt";
    private static final long MaxLogFileSize = 2L * 1024L * 1024L;//2MB
    private static final int MaxLogQueueLength = 1000;
    private static final long OverdueLimit = 5L * 24L * 60L * 60L * 1000L;
    private static final SimpleDateFormat LogFormat = new SimpleDateFormat("yyyy:MM:dd kk:mm:ss.SSS");
    private static final int MaxLogBufferWriteCount = 20;

    private static ConcurrentLinkedQueue<LogMsg> LogMsgQueue = new ConcurrentLinkedQueue<>();
    private static AtomicInteger LogMsgQueueLength = new AtomicInteger(0);

    private static final Handler LogHandler;
    private static final HandlerThread LogThread;
    private static Runnable LogWriteRunnable = null;

    private static StringBuffer LogBuffer = new StringBuffer();
    private static int LogBufferWriteIndex = 0;

    static {
        LogThread = new HandlerThread("GlobalStartUpLogWriterThread");
        LogThread.start();

        LogHandler = new Handler(LogThread.getLooper());
        LogHandler.post(new Runnable() {
            @Override
            public void run() {
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
            }
        });
    }

    public static void writeLogToFile(String log) {
        if (!StoreExist.get()) {
            return;
        }

        if (MaxLogQueueLength < LogMsgQueueLength.get()) {
            return;
        }

        LogMsgQueueLength.incrementAndGet();

        LogMsg msg = new LogMsg();
        msg.mMsg = log;
        msg.mDate = new Date();

        LogMsgQueue.add(msg);

        if (null == LogWriteRunnable) {
            LogWriteRunnable = new Runnable() {
                @Override
                public void run() {
                    LogMsg logMsg = LogMsgQueue.poll();
                    if (null != logMsg) {
                        LogMsgQueueLength.decrementAndGet();
                    }

                    while (null != logMsg) {
                        try {
                            writeToFile(logMsg.mMsg, logMsg.mDate);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        logMsg = LogMsgQueue.poll();
                        if (null != logMsg) {
                            LogMsgQueueLength.decrementAndGet();
                        }
                    }

                    LogWriteRunnable = null;
                }
            };

            LogHandler.post(LogWriteRunnable);
        }
    }

    /**
     * Only an emergency can call such as crash
     *
     * @hide
     */
    static void writeToFileImmediately() {
        if (null == RootDir || !RootDir.exists()) {
            AdaUtils.crashIfDebug("writeToFileImmediately RootDir error");
            return;
        }

        String dir = RootDir.getAbsolutePath() + LogPath;
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File logFile = new File(dir + File.separator + LogName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            fileWriter.write(LogBuffer.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            AdaUtils.crashIfDebug("writeToFileImmediately but got error %s", e.toString());
        }
    }

    /**
     * Only an emergency can call such as crash
     *
     * @hide
     */
    static void writeUnCaughtException(String msg, Date date) {
        File logFile = obtainLogFile(UnCaughtException, date);
        if (null == logFile) {
            return;
        }

        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            StringBuffer buffer = new StringBuffer();
            LogFormat.format(date, buffer, new FieldPosition(0));
            buffer.append('\n');
            buffer.append(msg);
            fileWriter.write(buffer.toString());
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            AdaUtils.crashIfDebug("writeUnCaughtException but got error %s", e.toString());
        }
    }

    static void getLogRar(final OnZipLogFiles callback) {
        if (!StoreExist.get()) {
            callback.onFailed();
            return;
        }

        LogHandler.post(new Runnable() {
            @Override
            public void run() {
                if (null == RootDir || !RootDir.exists()) {
                    callback.onFailed();
                    return;
                }

                String absDir = RootDir.getAbsolutePath() + LogPath;
                File dirFile = new File(absDir);
                if (!dirFile.exists() || !dirFile.isDirectory()) {
                    callback.onFailed();
                    return;
                }

                File[] files = dirFile.listFiles();
                List<File> fileList = new ArrayList<>();
                for (File item : files) {
                    String fileName = item.getName();
                    if (0 == fileName.compareTo(LogName)
                            || 0 == fileName.compareTo(UnCaughtException)) {
                        fileList.add(item);
                        continue;
                    }

                    if (fileName.endsWith(".back")) {
                        long lag = System.currentTimeMillis() - item.lastModified();
                        if (lag > 0L && 1L * 24L * 60L * 60L * 1000L > lag) {
                            fileList.add(item);
                        }
                    }
                }

                if (fileList.isEmpty()) {
                    callback.onFailed();
                    return;
                }

                File zip = FileUtils.zipFiles(absDir + File.separator + "logs.rar", fileList);
                if (null == zip) {
                    callback.onFailed();
                } else {
                    callback.onSuccess(zip);
                }
            }
        });
    }

    private static void writeToFile(String msg, Date date) throws IOException {
        LogFormat.format(date, LogBuffer, new FieldPosition(0));
        LogBuffer.append(' ');
        LogBuffer.append(msg);
        LogBuffer.append('\n');

        LogBufferWriteIndex += 1;

        if (MaxLogBufferWriteCount < LogBufferWriteIndex) {

            File logFile = obtainLogFile(LogName, date);
            if (null != logFile) {
                FileWriter fileWriter = new FileWriter(logFile, true);
                fileWriter.write(LogBuffer.toString());
                fileWriter.flush();
                fileWriter.close();
            }

            LogBufferWriteIndex = 0;
            LogBuffer.delete(0, LogBuffer.length());
        }
    }

    private static File obtainLogFile(String fileName, Date date) {
        if (null == RootDir || !RootDir.exists()) {
            AdaUtils.crashIfDebug("obtain log file failed RootDir error");
            return null;
        }

        String dir = RootDir.getAbsolutePath() + LogPath;
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            dirFile.mkdirs();
        }

        File logFile = new File(dir + File.separator + fileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                AdaUtils.crashIfDebug("obtain log file failed %s", e.toString());
                return null;
            }
        } else {
            if (logFile.length() > MaxLogFileSize) {
                deleteOverdueLogFile();

                SimpleDateFormat simpleDateFormate = new SimpleDateFormat("-MM-dd-kk-mm-ss");
                String fileExt = simpleDateFormate.format(date);

                StringBuilder backFile = new StringBuilder(dir);
                backFile.append(File.separator).append(fileName).append(fileExt).append(".back");

                File fileNameTo = new File(backFile.toString());
                logFile.renameTo(fileNameTo);
            }
        }

        return logFile;
    }

    private static void deleteOverdueLogFile() {
        if (null == RootDir || !RootDir.exists()) {
            return;
        }

        String dir = RootDir.getAbsolutePath() + LogPath;
        File dirFile = new File(dir);
        if (!dirFile.exists()) {
            return;
        }

        long now = System.currentTimeMillis();
        File files[] = dirFile.listFiles();
        if (null != files) {
            for (File file : files) {
                if (file.getName().endsWith(".back")) {
                    long lastModifiedTime = file.lastModified();
                    if (OverdueLimit < now - lastModifiedTime) {
                        file.delete();
                    }
                }
            }
        }
    }

    private static class LogMsg {
        public String mMsg;
        public Date mDate;
    }

    public interface OnZipLogFiles {
        void onSuccess(File zipFile);

        void onFailed();
    }
}
