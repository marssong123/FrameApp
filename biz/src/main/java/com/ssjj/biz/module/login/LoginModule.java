package com.ssjj.biz.module.login;

import android.content.Intent;

import com.ssjj.ioc.module.AdaModule;
import com.ssjj.biz.module.umengsocial.UmengModule;
import com.ssjj.biz.ui.widget.BizActivity;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * Created by songmars on 16/5/26
 */

public class LoginModule extends AdaModule {
    private static final String TAG = "LoginModule";

    public enum LoginStatus {
        Unknown,
        LogOut,
        UserLogin,
        ThirdLogin
    }

    public enum LoginPlatform {
        QQ(SHARE_MEDIA.QQ),
        WeiXin(SHARE_MEDIA.WEIXIN);

        SHARE_MEDIA mPlatform;

        LoginPlatform(SHARE_MEDIA platform) {
            mPlatform = platform;
        }

        public SHARE_MEDIA value() {
            return mPlatform;
        }

        static LoginPlatform fromUMengPlatform(SHARE_MEDIA platform) {
            for (LoginPlatform item : LoginPlatform.values()) {
                if (platform == item.mPlatform) {
                    return item;
                }
            }

            return null;
        }
    }

    public static void loginByThirdPlatform(BizActivity activity, SHARE_MEDIA mPlatform , UmengModule.IAuthListener iAuthListener){

        UmengModule.loginByThirdPlatform(activity,mPlatform,iAuthListener );


    }


    public static void onActivityResult(BizActivity activity, int requestCode, int resultCode, Intent data) {
        UmengModule.onActivityResult(activity, requestCode, resultCode, data);
    }
}
