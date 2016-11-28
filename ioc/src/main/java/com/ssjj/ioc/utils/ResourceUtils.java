package com.ssjj.ioc.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.ssjj.ioc.log.L;

/**
 * Created by GZ1581 on 2016/5/13.
 */
public final class ResourceUtils {

    public static String getMetaValue(Context context, String key) {
        return getMetaValue(context, key, "");
    }

    public static synchronized String getMetaValue(Context context, String key, String defaultValue) {
        ApplicationInfo appInfo;
        try {
            appInfo = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (Throwable e) {
            return defaultValue;
        }

        Bundle bundle = appInfo.metaData;
        if (bundle == null) {
            return defaultValue;
        }
        String valueString = bundle.getString(key, "");
        if (null != valueString && !valueString.isEmpty()) {
            return valueString;
        }

        int valueInt = bundle.getInt(key, 0);

        return 0 == valueInt ? defaultValue : String.valueOf(valueInt);
    }

    public static boolean isDebugMode(Context context) {
        boolean debuggable = false;
        ApplicationInfo appInfo = null;
        PackageManager packageManager = context.getPackageManager();
        try {
            appInfo = packageManager.getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("ResourceUtils", e.toString());

            e.printStackTrace();
        }
        if (appInfo != null) {
            debuggable = (0 < (appInfo.flags & ApplicationInfo.FLAG_DEBUGGABLE));
        }

        Log.v("ResourceUtils", "isDebugMode debuggable: " + debuggable);

        return debuggable;
    }

    public static String getStringByName(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "string", context.getPackageName());
        if (0 < id) {
            return context.getString(id);
        }

        L.error(context, "can not find string by name : %s", name);

        return "";
    }

    public static int getDrawableIdByName(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        if (0 == id) {
            L.error(context, "can not find drawable id by name : %s", name);
        }

        return id;
    }

    public static int getXmlIdByName(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "xml", context.getPackageName());
        if (0 == id) {
            L.error(context, "can not find xml id by name : %s", name);
        }

        return id;
    }

    public static int getIdByIdName(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "id", context.getPackageName());
        if (0 == id) {
            L.error(context, "can not find id by id name : %s", name);
        }

        return id;
    }

    public static int getDimenIdByName(Context context, String name) {
        int id = context.getResources().getIdentifier(name, "dimen", context.getPackageName());
        if(0==id) {
            L.error(context, "can not find dimen id by name %s", name);
        }

        return id;
    }
}
