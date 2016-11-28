package com.ssjj.ioc.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ssjj.ioc.application.IocApplication;
import com.ssjj.ioc.module.network.NetWorkModule;

/**
 * Created by GZ1581 on 2016/5/23
 */

public final class NetWorkUtils {

    public static NetWorkModule.NetType getNetWorkStatus() {
        ConnectivityManager connManager = (ConnectivityManager) IocApplication.gContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null == connManager) {
            return NetWorkModule.NetType.None;
        }

        NetworkInfo info = connManager.getActiveNetworkInfo();
        if (null == info) {
            return NetWorkModule.NetType.None;
        }

        if (ConnectivityManager.TYPE_WIFI == info.getType()) {
            return NetWorkModule.NetType.Wifi;
        }

        if (ConnectivityManager.TYPE_MOBILE == info.getType()) {
            return NetWorkModule.NetType.Mobile;
        }

        return NetWorkModule.NetType.None;
    }

    public static boolean isNetWorkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) IocApplication.gContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        return null != info && (info.isConnected() || (info.isAvailable() && info.isConnectedOrConnecting()));
    }

}
