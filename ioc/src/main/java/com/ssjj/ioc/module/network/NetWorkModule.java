package com.ssjj.ioc.module.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.ssjj.ioc.application.IocApplication;
import com.ssjj.ioc.module.AdaModule;
import com.ssjj.ioc.property.AdaProperties;
import com.ssjj.ioc.utils.NetWorkUtils;

/**
 * Created by GZ1581 on 2016/5/23
 */

public final class NetWorkModule extends AdaModule {
    public enum NetType {
        None,
        Wifi,
        Mobile
    }

    private BroadcastReceiver mReceiver;

    public NetWorkModule() {
        AdaProperties.NetWorkType.set(NetType.None);
        AdaProperties.NetWorkAvailable.set(NetWorkUtils.isNetWorkAvailable());

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                AdaProperties.NetWorkType.set(NetWorkUtils.getNetWorkStatus());
                AdaProperties.NetWorkAvailable.set(NetWorkUtils.isNetWorkAvailable());
            }
        };

        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        IocApplication.gContext.registerReceiver(mReceiver, filter);
    }
}
