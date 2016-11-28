package com.ssjj.ioc.application;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.ssjj.ioc.iocvalue.IocValue;
import com.ssjj.ioc.event.EventCenterProxy;
import com.ssjj.ioc.log.L;
import com.ssjj.ioc.module.ModuleCenter;
import com.ssjj.ioc.module.network.NetWorkModule;

/**
 * Created by GZ1581 on 2016/5/13
 */

public class IocApplication extends Application {
    private static final String TAG = "IocApplication";

    public static Application gContext;

    private static Handler gMainHandler;

    public IocApplication() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        gContext = this;
        gMainHandler = new Handler(Looper.getMainLooper());

        IocValue.init(this);

        onInitCrashCatch();

        EventCenterProxy.init();
        ModuleCenter.init();

        onRegisterModules();

        ModuleCenter.startUp();
    }

    @Override
    public void onTerminate() {
        L.warn(TAG, "onTerminate");
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        L.warn(TAG, "onLowMemory!");
        super.onLowMemory();
    }

    protected void onRegisterModules() {
        ModuleCenter.register(NetWorkModule.class);
    }

    protected void onInitCrashCatch() {

    }

    public static void runAsync(Runnable runnable) {
        gMainHandler.post(runnable);
    }

    public static void runAsyncDelay(Runnable runnable, long delayMillis) {
        gMainHandler.postDelayed(runnable, delayMillis);
    }

    public static void removeRunAsync(Runnable runnable) {
        gMainHandler.removeCallbacks(runnable);
    }
}
