package com.ssjj.ioc.log;

import android.util.Log;

import com.ssjj.ioc.application.IocApplication;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;

/**
 * Created by GZ1581 on 2016/5/13
 */

public final class L {

    // can be overwrite in ada value
    public static int LOG_LEVEL = Log.INFO;
    public static String TAG = "Ioc";
    public static boolean LOG_ENABLE = true;

    public static boolean isLogLevelEnabled(int level) {
        return level >= LOG_LEVEL && LOG_ENABLE;
    }

    public static void verbose(Object object, String msg) {
        if (isLogLevelEnabled(Log.VERBOSE)) {
            verboseOut(object, msg, getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void verbose(Object object, String format, Object... args) {
        if (isLogLevelEnabled(Log.VERBOSE)) {
            verboseOut(object, String.format(format, args), getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void debug(Object object, String msg) {
        if (isLogLevelEnabled(Log.DEBUG)) {
            debugOut(object, msg, getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void debug(Object object, String format, Object... args) {
        if (isLogLevelEnabled(Log.DEBUG)) {
            debugOut(object, String.format(format, args), getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void info(Object object, String msg) {
        if (isLogLevelEnabled(Log.INFO)) {
            infoOut(object, msg, getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void info(Object object, String format, Object... args) {
        if (isLogLevelEnabled(Log.INFO)) {
            infoOut(object, String.format(format, args), getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void warn(Object object, String msg) {
        if (isLogLevelEnabled(Log.WARN)) {
            warnOut(object, msg, getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void warn(Object object, String format, Object... args) {
        if (isLogLevelEnabled(Log.WARN)) {
            warnOut(object, String.format(format, args), getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void error(Object object, String msg) {
        if (isLogLevelEnabled(Log.ERROR)) {
            errorOut(object, msg, getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void error(Object object, String format, Object... args) {
        if (isLogLevelEnabled(Log.ERROR)) {
            errorOut(object, String.format(format, args), getCallerFileName(), getCallerLineIndex());
        }
    }

    /**
     * Only an emergency can call such as crash
     *
     * @hide
     */
    public static void unCaughtException(Throwable t) {
        LogToES.writeToFileImmediately();

        StringWriter stackWrite = new StringWriter();
        t.printStackTrace(new PrintWriter(stackWrite));
        stackWrite.append("\n\n");
        String exception = stackWrite.toString();
        LogToES.writeUnCaughtException(exception, new Date());
    }

    public static void getLogRar(final LogToES.OnZipLogFiles callback) {
        LogToES.getLogRar(new LogToES.OnZipLogFiles() {
            @Override
            public void onSuccess(final File zipFile) {
                IocApplication.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(zipFile);
                    }
                });
            }

            @Override
            public void onFailed() {
                IocApplication.runAsync(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed();
                    }
                });
            }
        });
    }

    private static void verboseOut(Object object, String msg, String fileName, int lineIndex) {
        String logText = formatLogMsg(object, msg, fileName, lineIndex);

        Log.v(TAG, logText);
        logToFile(String.format("VERBOSE %s", logText));
    }

    private static void debugOut(Object object, String msg, String fileName, int lineIndex) {
        String logText = formatLogMsg(object, msg, fileName, lineIndex);

        Log.d(TAG, logText);
        logToFile(String.format("DEBUG %s", logText));
    }

    private static void infoOut(Object object, String msg, String fileName, int lineIndex) {
        String logText = formatLogMsg(object, msg, fileName, lineIndex);

        Log.i(TAG, logText);
        logToFile(String.format("INFO %s", logText));
    }

    private static void warnOut(Object object, String msg, String fileName, int lineIndex) {
        String logText = formatLogMsg(object, msg, fileName, lineIndex);

        Log.w(TAG, logText);
        logToFile(String.format("WARN %s", logText));
    }

    private static void errorOut(Object object, String msg, String fileName, int lineIndex) {
        String logText = formatLogMsg(object, msg, fileName, lineIndex);

        Log.e(TAG, logText);
        logToFile(String.format("ERROR %s", logText));
    }

    private static void logToFile(String logText) {
        LogToES.writeLogToFile(logText);
    }

    private static String formatLogMsg(Object object, String msg, String fileName, int lineIndex) {
        return String.format("%s, P: %d, T: %d, C: %s, at %s: %d", msg
                , android.os.Process.myPid(), Thread.currentThread().getId()
                , objectClassName(object), fileName, lineIndex);
    }

    private static int getCallerLineIndex() {
        return Thread.currentThread().getStackTrace()[4].getLineNumber();
    }

    private static String getCallerFileName() {
        return Thread.currentThread().getStackTrace()[4].getFileName();
    }

    private static String getCallerMethodName() {
        return Thread.currentThread().getStackTrace()[4].getMethodName();
    }

    private static String objectClassName(Object object) {
        if (null == object) {
            return "Global";
        }

        return (object instanceof String) ? (String) object : object.getClass().getSimpleName();
    }
}
