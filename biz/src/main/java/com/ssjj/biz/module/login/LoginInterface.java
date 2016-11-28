package com.ssjj.biz.module.login;

import com.ssjj.ioc.event.signal.EventSignal;
import com.ssjj.biz.ui.widget.BizActivity;

import java.lang.ref.WeakReference;

/**
 * Created by GZ1581 on 2016/5/28
 */

public interface LoginInterface {
    final class ThirdLogin extends EventSignal {
        LoginModule.LoginPlatform mPlatform;
        WeakReference<BizActivity> mActivity;

        public ThirdLogin(BizActivity activity, LoginModule.LoginPlatform platform) {
            mPlatform = platform;
            mActivity = new WeakReference<>(activity);
        }
    }
}
