package com.ssjj.biz.module.share;

import com.ssjj.ioc.event.signal.EventSignal;

/**
 * Created by GZ1581 on 2016/5/25
 */

public interface ShareCallback {
    final class ShareResult extends EventSignal {
        public static final int NoPackage = -1;
        public static final int Success = 0;
        public static final int Failed = 1;
        public static final int Cancel = 2;

        private int mResult;
        private ShareModule.SharePlatform mPlatform;
        private String mError;

        ShareResult(int result, ShareModule.SharePlatform platform, String error) {
            mResult = result;
            mPlatform = platform;
            mError = error;
        }

        public int getResult() {
            return mResult;
        }

        public ShareModule.SharePlatform getPlatform() {
            return mPlatform;
        }

        public String getError() {
            return mError;
        }
    }
}
