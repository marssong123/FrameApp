package com.ssjj.ioc.log;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.ssjj.ioc.iocvalue.IocValue;

/**
 * Created by GZ1581 on 2016/5/16
 */
public final class UILog {

    public static void lifeCycle(Activity activity, String info) {
        if(IocValue.isDebuggable()) {
            L.info(activity, "lifeCycle activity %s", info);
        }
    }

    public static void lifeCycle(Fragment fragment, String info) {
        if(IocValue.isDebuggable()) {
            L.info(fragment, "lifeCycle fragment %s", info);
        }
    }
}
