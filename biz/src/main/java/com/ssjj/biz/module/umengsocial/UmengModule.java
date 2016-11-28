package com.ssjj.biz.module.umengsocial;

import android.app.ProgressDialog;
import android.content.Intent;

import com.ssjj.ioc.iocvalue.IocValue;
import com.ssjj.ioc.log.L;
import com.ssjj.biz.R;
import com.ssjj.biz.application.BizApplication;
import com.ssjj.biz.module.share.ShareModule;
import com.ssjj.biz.ui.widget.BizActivity;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.common.SocializeConstants;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.utils.Log;

/**
 * Created by GZ1581 on 2016/5/26
 */
public final class UmengModule {
    private static final String TAG = "UmengModule";

    static {
        init();
    }

    public static void share(BizActivity activity, ShareModule.Params params, IShareListener l) {
        boolean has = UMShareAPI.get(activity).isInstall(activity, params.getPlatform().value());
        if (!has) {
            l.onNoPackage(params.getPlatform());
            L.warn(TAG, "no package %s", params.getPlatform().name());
            return;
        }

        ShareAction action = new ShareAction(activity);
        action.setPlatform(params.getPlatform().value());
        if (null != params.getTitle()) {
            action.withTitle(params.getTitle());
            L.info(TAG, "share write title %s", params.getTitle());
        }

        if (null != params.getText()) {
            action.withText(params.getText());
            L.info(TAG, "share write text %s", params.getText());
        }

        if (null != params.getUrl()) {
            action.withTargetUrl(params.getUrl());
            L.info(TAG, "share write url %s", params.getUrl());
        }

        if (null != params.getImageUrl()) {
            UMImage image = new UMImage(activity, params.getImageUrl());
            action.withMedia(image);
            L.info(TAG, "share write image url %s", params.getImageUrl());
        }

        action.setCallback(l);

        ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.sharing));
        dialog.setCanceledOnTouchOutside(false);

        Config.dialog = dialog;

        action.share();
    }

    public static void onActivityResult(BizActivity activity, int requestCode, int resultCode, Intent data) {
        try {
            UMShareAPI.get(activity).onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            L.error(TAG, "umeng activity resule exception %s", e.toString());
        }
    }

    public static void loginByThirdPlatform(BizActivity activity, SHARE_MEDIA platform, IAuthListener l) {
        boolean has = UMShareAPI.get(activity).isInstall(activity, platform);
        if (!has) {
            l.onNoPackage(platform);
            L.warn(TAG, "no package %s", platform.name());
            return;
        }

        UMShareAPI.get(activity).doOauthVerify(activity, platform, l);
    }

    public interface IShareListener extends UMShareListener {
        void onNoPackage(ShareModule.SharePlatform platform);
    }

    public interface IAuthListener extends UMAuthListener {
        void onNoPackage(SHARE_MEDIA platform);
    }

    private static void init() {
        SocializeConstants.APPKEY = BizApplication.gContext.getString(IocValue.isDebuggable() ?
                R.string.umeng_app_key_debug : R.string.umeng_app_key_release);

        //微信 appid appsecret
        PlatformConfig.setWeixin(BizApplication.gContext.getString(R.string.weixin_appid), BizApplication.gContext.getString(R.string.weixin_appsecret));
        // QQ和Qzone appid appkey
        PlatformConfig.setQQZone(BizApplication.gContext.getString(R.string.qq_appid), BizApplication.gContext.getString(R.string.qq_appkey));

        Config.IsToastTip = false;
        Log.LOG = IocValue.isDebuggable();
    }
}
