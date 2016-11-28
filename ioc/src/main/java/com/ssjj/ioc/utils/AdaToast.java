package com.ssjj.ioc.utils;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.ssjj.ioc.R;
import com.ssjj.ioc.application.IocApplication;
import com.ssjj.ioc.log.L;

/**
 * Created by GZ1581 on 2016/5/25
 */

public final class AdaToast {
    public enum Duration {
        SHORT(Toast.LENGTH_SHORT),
        LONG(Toast.LENGTH_LONG);

        Duration(int value) {
            mValue = value;
        }

        int mValue;
    }

    private static final String TAG = "AdaToast";

    private static Toast gToast;

    public static void show(int resId) {
        show(IocApplication.gContext.getString(resId));
    }

    public static void show(String text) {
        show(text, Duration.SHORT);
    }

    public static void show(int resId, Duration duration) {
        show(IocApplication.gContext.getString(resId), duration);
    }

    public static void show(String text, Duration duration) {
        show(text, duration, 0, 0);
    }

    public static void show(String text, Duration duration, int xOff, int yOff) {
        if (null == gToast) {
            L.info(TAG, "create ada toast");
            gToast = Toast.makeText(IocApplication.gContext, "", Toast.LENGTH_SHORT);
            View view = LayoutInflater.from(IocApplication.gContext).inflate(R.layout.ada_toast, null);
            gToast.setView(view);
        }

        try {
            gToast.setGravity(Gravity.CENTER, xOff, yOff);
            gToast.setText(text);
            gToast.setDuration(duration.mValue);
            gToast.show();
        } catch (Exception e) {
            L.error(TAG, "show ada toast error %s", e.toString());
        }
    }
}
