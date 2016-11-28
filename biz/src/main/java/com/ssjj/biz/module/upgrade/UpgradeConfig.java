package com.ssjj.biz.module.upgrade;

import android.content.Context;
import android.content.SharedPreferences;

import com.ssjj.ioc.utils.Version;
import com.ssjj.ioc.utils.VersionUtils;
import com.ssjj.biz.application.BizApplication;

/**
 * Created by GZ1581 on 2016/6/6
 */
public final class UpgradeConfig {
    private static final String TAG = "UpgradeConfig";
    private static final String IgnoreVersion = "ignoreVersion";
    private static final String PromptTime = "promptTime";

    static Version getIgnoreVersion() {
        SharedPreferences setting = BizApplication.gContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        String ver = setting.getString(IgnoreVersion, "");
        if (ver.isEmpty()) {
            return null;
        }

        return VersionUtils.getVerFromStr(ver);
    }

    static void setIgnoreVersion(Version version) {
        SharedPreferences setting = BizApplication.gContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        setting.edit().putString(IgnoreVersion, version.toString()).commit();
    }

    static int getPromptTime() {
        SharedPreferences setting = BizApplication.gContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        return setting.getInt(PromptTime, 0);
    }

    static void savePromptTime(int promptTime) {
        SharedPreferences setting = BizApplication.gContext.getSharedPreferences(TAG, Context.MODE_PRIVATE);
        setting.edit().putInt(PromptTime, promptTime).commit();
    }
}
