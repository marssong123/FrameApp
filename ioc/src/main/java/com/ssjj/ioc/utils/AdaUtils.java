package com.ssjj.ioc.utils;

import android.util.Log;

import com.ssjj.ioc.iocvalue.IocValue;
import com.ssjj.ioc.log.L;

/**
 * Created by GZ1581 on 2016/5/17
 */

public final class AdaUtils {
    private static final String TAG = "AdaUtils";

    public static void crashIfDebug(String format, Object... args) {
        String msg = String.format(format, args);
        Log.e(TAG, String.format("ada crash if debug %s", msg));

        L.error(TAG, msg);

        if (IocValue.isDebuggable()) {
            throw new RuntimeException(String.format("ada crash if debug %s", msg));
        }
    }

    public static String bytesToHexString(byte[] bytes) {
        if (bytes == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (byte b : bytes) {
            int val = b & 0xff;
            if (val < 0x10) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString();
    }
}
