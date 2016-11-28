package com.ssjj.biz.module.report;

import com.ssjj.ioc.event.annotation.IASlot;
import com.ssjj.ioc.module.AdaModule;
import com.ssjj.biz.module.statistics.umeng.UmengModule;

/**
 * Created by GZ1581 on 2016/5/23
 */
//base on umeng module
public final class ReportModule extends AdaModule {

    public ReportModule() {
        super();
    }

    @IASlot
    public void onActivityStatus(ReportCall.ActivityStatus status) {
        UmengModule.reportActivityStatus(status.mActivity, status.mResume);
    }

    @IASlot
    public void onSignIn(ReportCall.SignIn signIn) {
        UmengModule.reportProfileSignIn(signIn.mId, signIn.mProvider);
    }

    @IASlot
    public void onSignOff(ReportCall.SignOff signOff) {
        UmengModule.reportSignOff();
    }

    @IASlot
    public void onReportEvent(ReportCall.ReportEvent event) {
        UmengModule.reportEvent(event.mId, event.mType);
    }

    @IASlot
    public void onReportError(ReportCall.ReportError error) {
        if (null != error.mError) {
            UmengModule.reportError(error.mError);
        } else if (null != error.mThrowable) {
            UmengModule.reportError(error.mThrowable);
        }
    }

    public static void onKillProcess() {
        UmengModule.onKillProcess();
    }
}
