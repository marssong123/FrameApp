package com.ssjj.mediasdk.log;

/**
 * Created by GZ1581 on 2016/6/21
 */

public final class Log {

    // can be overwrite in ada value
    private static int LOG_LEVEL = android.util.Log.INFO;
    private static boolean LOG_ENABLE = true;
    private static final String TAG = "MediaSDK";

    public static void init(int logLevel, boolean enable) {
        LOG_LEVEL = logLevel;
        LOG_ENABLE = enable;
    }

    public static boolean isLogLevelEnabled(int level) {
        return level >= LOG_LEVEL && LOG_ENABLE;
    }

    public static void verbose(Object object, String msg) {
        if (isLogLevelEnabled(android.util.Log.VERBOSE)) {
            verboseOut(object, msg, getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void verbose(Object object, String format, Object... args) {
        if (isLogLevelEnabled(android.util.Log.VERBOSE)) {
            verboseOut(object, String.format(format, args), getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void debug(Object object, String msg) {
        if (isLogLevelEnabled(android.util.Log.DEBUG)) {
            debugOut(object, msg, getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void debug(Object object, String format, Object... args) {
        if (isLogLevelEnabled(android.util.Log.DEBUG)) {
            debugOut(object, String.format(format, args), getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void info(Object object, String msg) {
        if (isLogLevelEnabled(android.util.Log.INFO)) {
            infoOut(object, msg, getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void info(Object object, String format, Object... args) {
        if (isLogLevelEnabled(android.util.Log.INFO)) {
            infoOut(object, String.format(format, args), getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void warn(Object object, String msg) {
        if (isLogLevelEnabled(android.util.Log.WARN)) {
            warnOut(object, msg, getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void warn(Object object, String format, Object... args) {
        if (isLogLevelEnabled(android.util.Log.WARN)) {
            warnOut(object, String.format(format, args), getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void error(Object object, String msg) {
        if (isLogLevelEnabled(android.util.Log.ERROR)) {
            errorOut(object, msg, getCallerFileName(), getCallerLineIndex());
        }
    }

    public static void error(Object object, String format, Object... args) {
        if (isLogLevelEnabled(android.util.Log.ERROR)) {
            errorOut(object, String.format(format, args), getCallerFileName(), getCallerLineIndex());
        }
    }

    private static void verboseOut(Object object, String msg, String fileName, int lineIndex) {
        String logText = formatLogMsg(object, msg, fileName, lineIndex);

        android.util.Log.v(TAG, logText);
        logToFile(String.format("VERBOSE %s", logText));
    }

    private static void debugOut(Object object, String msg, String fileName, int lineIndex) {
        String logText = formatLogMsg(object, msg, fileName, lineIndex);

        android.util.Log.d(TAG, logText);
        logToFile(String.format("DEBUG %s", logText));
    }

    private static void infoOut(Object object, String msg, String fileName, int lineIndex) {
        String logText = formatLogMsg(object, msg, fileName, lineIndex);

        android.util.Log.i(TAG, logText);
        logToFile(String.format("INFO %s", logText));
    }

    private static void warnOut(Object object, String msg, String fileName, int lineIndex) {
        String logText = formatLogMsg(object, msg, fileName, lineIndex);

        android.util.Log.w(TAG, logText);
        logToFile(String.format("WARN %s", logText));
    }

    private static void errorOut(Object object, String msg, String fileName, int lineIndex) {
        String logText = formatLogMsg(object, msg, fileName, lineIndex);

        android.util.Log.e(TAG, logText);
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
