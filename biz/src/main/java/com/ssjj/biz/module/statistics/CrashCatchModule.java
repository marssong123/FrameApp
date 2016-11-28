package com.ssjj.biz.module.statistics;

import com.ssjj.ioc.log.L;
import com.ssjj.biz.module.statistics.bugly.BuglyModule;
import com.ssjj.biz.module.statistics.umeng.UmengModule;

/**
 * Created by GZ1581 on 2016/5/20
 */
public final class CrashCatchModule {
    private static Thread.UncaughtExceptionHandler BuglyHandler;
    private static Thread.UncaughtExceptionHandler CrashHandler;

    public static void init() {
        BuglyModule.init();
        BuglyHandler = Thread.getDefaultUncaughtExceptionHandler();

        UmengModule.init();

        CrashHandler = new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                L.error("CrashCatchModule", "get error %s", ex.toString());
                L.unCaughtException(ex);

                UmengModule.reportError(ex);

                BuglyHandler.uncaughtException(thread, ex);
            }
        };

        Thread.setDefaultUncaughtExceptionHandler(CrashHandler);
    }

}
