package com.ssjj.ioc.module;

import com.ssjj.ioc.event.EventCenterProxy;

/**
 * Created by GZ1581 on 2016/5/20
 */

public class AdaModule implements IAdaModule {
    private static final String BaseClassName = AdaModule.class.getName();

    public AdaModule() {

    }

    protected void onStart() {
        EventCenterProxy.register(this, BaseClassName);
    }

    protected void onStop() {
        EventCenterProxy.unRegister(this);
    }

}
