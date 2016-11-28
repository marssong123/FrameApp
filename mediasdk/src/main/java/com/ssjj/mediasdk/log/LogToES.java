package com.ssjj.mediasdk.log;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by GZ1581 on 2016/6/21
 */

public final class LogToES {

    public static AtomicBoolean StoreExist = new AtomicBoolean(false);
    public static File RootDir;
    public static String LogPath = "/mediasdk/logs";

    public static final String LogName = "MediaSDKLogs.txt";
    public static final long MaxLogFileSize = 2L * 1024L * 1024L;//2MB
    public static final int MaxLogQueueLength = 1000;
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
                Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
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
}
