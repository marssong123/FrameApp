package com.ssjj.biz.videoplayer;

import android.content.Context;
import android.content.SharedPreferences;

import com.ssjj.ioc.log.L;
import com.ssjj.biz.application.BizApplication;

/**
 * Created by GZ1581 on 2016/6/22
 */
public final class MediaCodecMonitor {
    private static final String TAG = "MediaCodecMonitor";

    private static final String KeyMediaCodecMonitor = "MediaCodecMonitor";
    private static final String KeyMediaCodecStatus = "MediaCodecStatus";

    private static final int MediaCodecUnknown = -1;
    private static final int MediaCodecStart = 0;
    private static final int MediaCodecCrash = 1;
    private static final int MediaCodecSuccess = 2;

    private Runnable mMonitorRunnable;

    public MediaCodecMonitor() {
        SharedPreferences preferences = getPreferences();
        int status = preferences.getInt(KeyMediaCodecStatus, MediaCodecUnknown);
        if (MediaCodecStart == status) {
            preferences.edit().putInt(KeyMediaCodecStatus, MediaCodecCrash).commit();
        }
    }

    public void start() {
        SharedPreferences preferences = getPreferences();
        if (MediaCodecCrash == preferences.getInt(KeyMediaCodecStatus, MediaCodecUnknown)) {
            L.error(TAG, "media crashed last time");
            return;
        }

        if (null != mMonitorRunnable) {
            BizApplication.removeRunAsync(mMonitorRunnable);
            mMonitorRunnable = null;
        }

        getPreferences().edit().putInt(KeyMediaCodecStatus, MediaCodecStart).commit();

        mMonitorRunnable = new Runnable() {
            @Override
            public void run() {
                getPreferences().edit().putInt(KeyMediaCodecStatus, MediaCodecSuccess).commit();
                mMonitorRunnable = null;
            }
        };

        BizApplication.runAsyncDelay(mMonitorRunnable, 10000);
    }

    public void stop() {
        if (null != mMonitorRunnable) {
            BizApplication.removeRunAsync(mMonitorRunnable);
            mMonitorRunnable = null;
        }
    }

    public boolean isMediaCodecSupport() {
        return MediaCodecCrash != getPreferences().getInt(KeyMediaCodecStatus, MediaCodecUnknown);
    }

    private SharedPreferences getPreferences() {
        return BizApplication.gContext.getSharedPreferences(KeyMediaCodecMonitor, Context.MODE_PRIVATE);
    }
}
