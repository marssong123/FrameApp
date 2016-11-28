package com.ssjj.ioc.iocvalue;

import android.util.Log;

import com.ssjj.ioc.application.IocApplication;
import com.ssjj.ioc.log.L;
import com.ssjj.ioc.log.LogToES;
import com.ssjj.ioc.utils.FileUtils;
import com.ssjj.ioc.utils.ResourceUtils;

import java.io.File;

/**
 * Created by GZ1581 on 2016/5/13
 */

public final class IocValue {
    public static String gTag = "TAG_ADA";

    private static boolean gDebuggable = false;

    public static void init(IocApplication context) {
        initTag(context);
        initDebuggable(context);
        initLogs(context);

        L.info(gTag, "application debuggable %b", isDebuggable());
    }

    public static boolean isDebuggable() {
        return gDebuggable;
    }

    private static void initTag(IocApplication context) {
        gTag = ResourceUtils.getMetaValue(context, "TAG");
        if (null == gTag || gTag.isEmpty()) {
            String[] packageNames = context.getPackageName().split("\\.");
            gTag = packageNames[packageNames.length - 1];
        }
    }

    private static void initDebuggable(IocApplication context) {
        gDebuggable = ResourceUtils.isDebugMode(context);
    }

    private static void initLogs(IocApplication context) {
        L.LOG_LEVEL = gDebuggable ? Log.VERBOSE : Log.INFO;
        L.LOG_ENABLE = true;
        L.TAG = gTag;

        LogToES.LogPath = String.format("/%s/logs", gTag);

        File root = FileUtils.getRootDir(context);
        if (null != root) {
            LogToES.StoreExist.set(FileUtils.isStorageExist(context));
            LogToES.RootDir = root.getParentFile();
        } else {
            LogToES.StoreExist.set(false);
            LogToES.RootDir = null;
        }
    }
}
