package com.ssjj.ioc.event.executor;

import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by GZ1581 on 2016/5/17/
 */

public final class ThreadPoolExecutor extends AbsExecutor {
    private static final BlockingQueue<Runnable> WorkQueue = new LinkedBlockingQueue<>(64);
    private static final ThreadFactory ThreadFactory = new ThreadFactory() {
        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override
        public Thread newThread(@NonNull Runnable r) {
            return new Thread(r, String.format("AdaEventThreadPoolExecutor %d", mCount.getAndIncrement()));
        }
    };

    private java.util.concurrent.ThreadPoolExecutor mExecutor;

    public ThreadPoolExecutor() {
        super(IExecutor.ThreadPool);

        int cpuCore = Runtime.getRuntime().availableProcessors();
        cpuCore = 0 >= cpuCore ? 1 : cpuCore;

        mExecutor = new java.util.concurrent.ThreadPoolExecutor(cpuCore + 1, 2 * cpuCore,
                30L, TimeUnit.SECONDS, WorkQueue, ThreadFactory);
    }

    @Override
    public void execute(@NonNull Runnable command) {
        mExecutor.execute(command);
    }
}
