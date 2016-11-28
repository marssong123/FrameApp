package com.ssjj.ioc.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;

import com.ssjj.ioc.application.IocApplication;

/**
 * Created by GZ1581 on 2016/6/8
 */

public final class ProgressNotify {

    private FrequentlySetter mSetter;
    private NotificationCompat.Builder mBuilder = null;
    private NotificationManager mManager = null;
    private int mMaxValue = 0;
    private int mNotifyId;

    public ProgressNotify(NotifyUtils.NotifyID id, int smallIconId, int largeIconId, int titleId, int contentId) {
        mSetter = new FrequentlySetter(new FrequentlySetter.OnSetListener() {
            @Override
            public void onStart() {
                mManager.notify(mNotifyId, mBuilder.build());
            }

            @Override
            public void onCancel() {
                mManager.cancel(mNotifyId);
                mMaxValue = 0;
            }

            @Override
            public void onValue(int value) {
                updateNotify(value);
            }
        });

        mNotifyId = id.getID();

        mManager = (NotificationManager) IocApplication.gContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(IocApplication.gContext);
        mBuilder.setAutoCancel(false);
        mBuilder.setOngoing(true);

        if (-1 != smallIconId) {
            mBuilder.setSmallIcon(smallIconId);
        }

        if (-1 != largeIconId) {
            mBuilder.setLargeIcon(BitmapFactory.decodeResource(IocApplication.gContext.getResources(), largeIconId));
        }

        if (-1 != titleId) {
            mBuilder.setContentTitle(IocApplication.gContext.getString(titleId));
        }

        if (-1 != contentId) {
            mBuilder.setContentText(IocApplication.gContext.getString(contentId));
        }
    }

    public void start(int maxValue) {
        mMaxValue = maxValue;
        mSetter.start();
    }

    public void cancel() {
        mSetter.cancel();
    }

    public void setProgress(int value) {
        mSetter.setValue(value);
    }

    private void updateNotify(int value) {
        if (0 >= mMaxValue) {
            return;
        }

        mBuilder.setProgress(100, (int) Math.ceil(value * 100.0f / mMaxValue), false);
        mManager.notify(mNotifyId, mBuilder.build());
    }
}
