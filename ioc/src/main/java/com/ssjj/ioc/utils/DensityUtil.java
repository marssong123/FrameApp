package com.ssjj.ioc.utils;

import com.ssjj.ioc.application.IocApplication;

/**
 * Created by GZ1581 on 2016/5/13.
 */

public final class DensityUtil {

    public static int dp2px(float dpValue) {
        float scale = IocApplication.gContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dp(float pxValue) {
        float scale = IocApplication.gContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int sp2px(float spValue) {
        float fontScale = IocApplication.gContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public static int px2sp(float pxValue) {
        float fontScale = IocApplication.gContext.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }
}
