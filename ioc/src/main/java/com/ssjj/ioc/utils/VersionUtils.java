package com.ssjj.ioc.utils;

import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by GZ1581 on 2016/5/20
 */

public final class VersionUtils {
    private static int LocalVersion[] = null;
    private static String LocalName = null;
    private static final String Dot = ".";

    public static Version getVerFromStr(String version) {
        if (version.matches("\\d{1,}\\.\\d{1,}\\.\\d{1,}")) {
            Version ver = new Version();
            int dotPos = version.indexOf(Dot);
            int prevPos = 0;
            ver.mMajor = Integer.valueOf(version.substring(prevPos, dotPos));
            prevPos = dotPos + 1;
            dotPos = version.indexOf(Dot, prevPos);
            ver.mMinor = Integer.valueOf(version.substring(prevPos, dotPos));
            prevPos = dotPos + 1;
            ver.mBuild = Integer.valueOf(version.substring(prevPos));
            return ver;
        }
        return null;
    }

    public static Version getLocalVer(Context c) {
        Version v = new Version();
        int ver[] = VersionUtils.getLocal(c);
        v.mMajor = ver[0];
        v.mMinor = ver[1];
        v.mBuild = ver[2];
        return v;
    }

    public static String getLocalName(Context c) {
        if (null != LocalName) {
            return LocalName;
        }

        loadLocalVersion(c);

        return LocalName;
    }

    public static int[] getLocal(Context c) {
        if (null != LocalVersion) {
            return LocalVersion;
        }

        loadLocalVersion(c);

        return LocalVersion;
    }

    private static void loadLocalVersion(Context c) {
        try {
            LocalName = c.getPackageManager().getPackageInfo(c.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("LocalVersion Package Error");
        }

        if (null == LocalName) {
            throw new RuntimeException("LocalVersion VersionName Not Exist");
        }

        // handle version like this "1.0.0-SNAPSHOT";
        int pos = LocalName.indexOf('-');
        if (-1 != pos) {
            LocalName = LocalName.substring(0, pos);
        }
        String verStr[] = LocalName.split("\\.");

        if (3 != verStr.length) {
            throw new RuntimeException("LocalVersion VersionName Error");
        }

        LocalVersion = new int[3];

        try {
            for (int i = 0; i < 3; i++) {
                LocalVersion[i] = Integer.parseInt(verStr[i]);
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("LocalVersion VersionName Error");
        }
    }
}
