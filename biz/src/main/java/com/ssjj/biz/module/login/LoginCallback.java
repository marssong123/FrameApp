package com.ssjj.biz.module.login;

import com.ssjj.ioc.event.signal.EventSignal;

import java.util.Map;

/**
 * Created by GZ1581 on 2016/5/28
 */

public interface LoginCallback {
    final class ThirdLoginResult extends EventSignal {
        public static final int UnKnown = -1;
        public static final int NoInstall = 0;
        public static final int Complete = 1;
        public static final int Error = 2;
        public static final int Cancel = 3;

        private int mResult;
        private Map<String, String> mData;

        private LoginModule.LoginPlatform mPlatform;

        ThirdLoginResult(int result, LoginModule.LoginPlatform platform, Map<String, String> data) {
            mData=data;
            mResult = result;
            mPlatform = platform;
        }

        public int getResult() {
            return mResult;
        }

        public  Map<String, String> getData(){
            return  mData;
        }

        public LoginModule.LoginPlatform getPlatform() {
            return mPlatform;
        }
    }
}
