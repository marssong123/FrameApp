package com.ssjj.biz.module.statistics.umeng;

import com.ssjj.ioc.iocvalue.IocValue;
import com.ssjj.biz.R;
import com.ssjj.biz.application.BizApplication;
import com.ssjj.biz.ui.widget.BizActivity;
import com.umeng.analytics.MobclickAgent;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GZ1581 on 2016/5/20
 */

public final class UmengModule {
    static {
        innerInit();
    }

    public static void init() {

    }

    public static void reportActivityStatus(BizActivity activity, boolean resume) {
        if (resume) {
            MobclickAgent.onResume(activity);
        } else {
            MobclickAgent.onPause(activity);
        }
    }

    //ID：用户账号ID，长度小于64字节
    //Provider：账号来源。如果用户通过第三方账号登陆，可以调用此接口进行统计。
    // 支持自定义，不能以下划线"_"开头，使用大写字母和数字标识，长度小于32 字节;
    public static void reportProfileSignIn(String id, String provider) {
        MobclickAgent.onProfileSignIn(provider, id);
    }

    public static void reportSignOff() {
        MobclickAgent.onProfileSignOff();
    }

    public static void reportEvent(String id, String type) {
        MobclickAgent.onEvent(BizApplication.gContext, id, type);
    }

    public static void reportEvent(String id, HashMap<String, String> map) {
        MobclickAgent.onEvent(BizApplication.gContext, id, map);
    }

    public static void reportValue(String id, Map<String, String> map, int value) {
        MobclickAgent.onEventValue(BizApplication.gContext, id, map, value);
    }

    public static void reportError(Throwable throwable) {
        MobclickAgent.reportError(BizApplication.gContext, throwable);
    }

    public static void reportError(String error) {
        MobclickAgent.reportError(BizApplication.gContext, error);
    }

    public static void onKillProcess() {
        MobclickAgent.onKillProcess(BizApplication.gContext);
    }

    private static void innerInit() {
        String appKey = IocValue.isDebuggable() ? BizApplication.gContext.getString(R.string.umeng_app_key_debug)
                : BizApplication.gContext.getString(R.string.umeng_app_key_release);

        MobclickAgent.UMAnalyticsConfig config = new MobclickAgent.UMAnalyticsConfig(
                BizApplication.gContext, appKey
                , BizApplication.getAppChannelName()
                , MobclickAgent.EScenarioType.E_UM_NORMAL, true);

        MobclickAgent.startWithConfigure(config);

        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.enableEncrypt(true);
        MobclickAgent.setCatchUncaughtExceptions(false);
    }
}
