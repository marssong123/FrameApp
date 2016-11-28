package com.ssjj.ioc.event.executor;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

/**
 * Created by GZ1581 on 2016/5/17
 */
public class MainThreadExecutor extends AbsExecutor {
    private Handler mHandler;

    public MainThreadExecutor() {
        super(IExecutor.MainThread);

        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mHandler.post(command);
    }
}
