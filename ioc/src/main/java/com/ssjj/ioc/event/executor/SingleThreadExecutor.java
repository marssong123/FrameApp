package com.ssjj.ioc.event.executor;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Process;
import android.support.annotation.NonNull;

/**
 * Created by GZ1581 on 2016/5/17
 */

public final class SingleThreadExecutor extends AbsExecutor {
    private Handler mHandler;

    public SingleThreadExecutor(String tag) {
        super(IExecutor.SingleThread);

        HandlerThread thread = new HandlerThread(String.format("AdaEventSingleThreadExecutorThread %s", tag)
                , Process.THREAD_PRIORITY_DEFAULT);
        thread.start();

        mHandler = new Handler(thread.getLooper());
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mHandler.post(command);
    }
}
