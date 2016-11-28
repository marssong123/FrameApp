package com.ssjj.ioc.event.executor;

import android.support.annotation.NonNull;

/**
 * Created by GZ1581 on 2016/5/17
 */

public final class SyncExecutor extends AbsExecutor {
    public SyncExecutor() {
        super(IExecutor.Sync);
    }

    @Override
    public void execute(@NonNull Runnable command) {
        command.run();
    }
}
