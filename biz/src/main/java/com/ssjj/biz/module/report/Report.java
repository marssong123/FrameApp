package com.ssjj.biz.module.report;

import com.ssjj.ioc.event.EventCenterProxy;
import com.ssjj.ioc.event.signal.SignalType;
import com.ssjj.biz.ui.widget.BizActivity;

/**
 * Created by GZ1581 on 2016/5/23
 */

public final class Report {

    public static void activityStatus(BizActivity activity, boolean resume) {
        EventCenterProxy.send(new ReportCall.ActivityStatus(activity, resume), SignalType.Sync);
    }

    public static void signIn(String id, String provider) {
        EventCenterProxy.send(new ReportCall.SignIn(id, provider));
    }

    public static void signOff() {
        EventCenterProxy.send(new ReportCall.SignOff());
    }

    public static void event(String id) {
        EventCenterProxy.send(new ReportCall.ReportEvent(id));
    }

    public static void event(String id, String type) {
        EventCenterProxy.send(new ReportCall.ReportEvent(id, type));
    }

    public static void error(String error) {
        EventCenterProxy.send(new ReportCall.ReportError(error), SignalType.Sync);
    }

    public static void error(Throwable error) {
        EventCenterProxy.send(new ReportCall.ReportError(error), SignalType.Sync);
    }
}
