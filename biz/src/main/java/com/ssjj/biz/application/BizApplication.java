package com.ssjj.biz.application;

import com.ssjj.ioc.iocvalue.IocValue;
import com.ssjj.ioc.application.IocApplication;
import com.ssjj.ioc.log.LogToES;
import com.ssjj.ioc.module.ModuleCenter;
import com.ssjj.ioc.utils.ResourceUtils;
import com.ssjj.biz.R;
import com.ssjj.biz.module.login.LoginModule;
import com.ssjj.biz.module.report.ReportModule;
import com.ssjj.biz.module.statistics.CrashCatchModule;
import com.ssjj.biz.module.upgrade.UpgradeModule;
import com.ssjj.mediasdk.MediaSDK;

/**
 * Created by GZ1581 on 2016/5/19
 */
public class BizApplication extends IocApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        MediaSDK.init(IocValue.isDebuggable());
        MediaSDK.initLog(LogToES.StoreExist.get(), LogToES.RootDir, LogToES.LogPath);
    }

    @Override
    public void onRegisterModules() {
        super.onRegisterModules();

        ModuleCenter.register(ReportModule.class);
        ModuleCenter.register(UpgradeModule.class, false);
        ModuleCenter.register(LoginModule.class);
    }

    @Override
    protected void onInitCrashCatch() {
        CrashCatchModule.init();
    }

    public static String getAppChannelName() {
        return ResourceUtils.getMetaValue(gContext, "app_channel_id", gContext.getString(R.string.app_channel_id));
    }
}
