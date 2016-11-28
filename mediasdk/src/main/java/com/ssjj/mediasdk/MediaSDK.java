package com.ssjj.mediasdk;

import com.ssjj.mediasdk.log.Log;
import com.ssjj.mediasdk.log.LogToES;

import java.io.File;

public final class MediaSDK {

    private static boolean Debuggable = true;

    public static boolean isDebuggable() {
        return Debuggable;
    }

    public static void init(boolean debuggable) {
        Debuggable = debuggable;
    }

    public static void initLog(boolean storeExist, File rootDir, String path) {
        Log.init(Debuggable ? android.util.Log.VERBOSE : android.util.Log.INFO, true);
        LogToES.StoreExist.set(storeExist);
        LogToES.RootDir = rootDir;
        LogToES.LogPath = path;
    }
}