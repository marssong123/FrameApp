package com.ssjj.biz.module.statistics.bugly;

import com.ssjj.ioc.iocvalue.IocValue;
import com.ssjj.ioc.utils.VersionUtils;
import com.ssjj.biz.R;
import com.ssjj.biz.application.BizApplication;
import com.tencent.bugly.crashreport.CrashReport;

/**
 * Created by GZ1581 on 2016/5/20
 */
public final class BuglyModule {
    static {
        innerInit();
    }

    public static void init() {

    }

    private static void innerInit() {
        String appId = IocValue.isDebuggable() ? BizApplication.gContext.getString(R.string.bugly_app_id_debug)
                : BizApplication.gContext.getString(R.string.bugly_app_id_release);

        CrashReport.UserStrategy userStrategy = new CrashReport.UserStrategy(BizApplication.gContext);
        userStrategy.setAppChannel(BizApplication.getAppChannelName());
        userStrategy.setAppVersion(VersionUtils.getLocalName(BizApplication.gContext));
        userStrategy.setEnableANRCrashMonitor(true);
        userStrategy.setEnableNativeCrashMonitor(true);

        CrashReport.initCrashReport(BizApplication.gContext, appId, IocValue.isDebuggable(), userStrategy);
    }
}
