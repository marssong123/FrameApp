package com.ssjj.ioc.module;

import com.ssjj.ioc.application.IocApplication;
import com.ssjj.ioc.log.L;
import com.ssjj.ioc.utils.AdaUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by GZ1581 on 2016/5/20
 */
public final class ModuleCenter {
    private static final String TAG = "ModuleCenter";

    private static ModuleCenter mInstance;

    private Map<String, AdaModule> mModules;
    private List<String> mRegisterSync;

    public static void init() {
        if (null == mInstance) {
            mInstance = new ModuleCenter();
        }
    }

    public static void register(Class<? extends AdaModule> module) {
        register(module, true);
    }

    public static void register(Class<? extends AdaModule> module, boolean async) {
        if (async) {
            mInstance.registerAsync(module);
        } else {
            mInstance.mRegisterSync.add(module.getName());
            mInstance.pRegister(module);
        }
    }

    public static void unRegister(Class<? extends AdaModule> module) {
        mInstance.unRegisterAsync(module);
    }

    public static void startUp() {
        mInstance.startUpModule();
    }

    public static void stop() {
        mInstance.stopModuleAsync();
    }

    public static <T extends AdaModule> T getModule(Class<T> module) {
        T value = (T) mInstance.getModule(module.getName());
        if (null == value) {
            AdaUtils.crashIfDebug("module %s have not been found", module.getName());
        }

        return value;
    }

    private ModuleCenter() {
        mModules = new HashMap<>();
        mRegisterSync = new ArrayList<>();
    }

    private void registerAsync(final Class<? extends AdaModule> module) {
        IocApplication.runAsync(new Runnable() {
            @Override
            public void run() {
                pRegister(module);
            }
        });
    }

    private void unRegisterAsync(final Class<? extends AdaModule> module) {
        IocApplication.runAsync(new Runnable() {
            @Override
            public void run() {
                pUnRegister(module);
            }
        });
    }

    private synchronized void startUpModule() {
        L.info(TAG, "modules start up");

        for (String item : mRegisterSync) {
            mModules.get(item).onStart();
        }

        IocApplication.runAsync(new Runnable() {
            @Override
            public void run() {
                Set<Map.Entry<String, AdaModule>> set = mModules.entrySet();
                for (Map.Entry<String, AdaModule> item : set) {
                    if (mRegisterSync.contains(item.getKey())) {
                        mRegisterSync.remove(item.getKey());
                    } else {
                        item.getValue().onStart();
                    }
                }

                mRegisterSync.clear();
            }
        });
    }

    private void stopModuleAsync() {
        IocApplication.runAsync(new Runnable() {
            @Override
            public void run() {
                pStopModule();
            }
        });
    }

    private synchronized void pRegister(Class<? extends AdaModule> module) {
        String key = module.getName();

        if (null != mModules.get(key)) {
            AdaUtils.crashIfDebug("already has module %s", key);
            return;
        }

        AdaModule instance;
        try {
            instance = module.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            L.error(TAG, "register module error %s %s", module.getName(), e.toString());
            return;
        }

        mModules.put(key, instance);
    }

    private synchronized void pUnRegister(Class<? extends AdaModule> module) {
        String key = module.getName();

        AdaModule instance = mModules.remove(key);
        if (null == instance) {
            AdaUtils.crashIfDebug("no module %s, but call unRegister", key);
        } else {
            instance.onStop();
        }
    }

    private synchronized void pStopModule() {
        L.info(TAG, "module stop");

        Set<Map.Entry<String, AdaModule>> set = mModules.entrySet();
        for (Map.Entry<String, AdaModule> item : set) {
            item.getValue().onStop();
        }
    }

    private synchronized AdaModule getModule(String name) {
        return mModules.get(name);
    }
}
