package com.ssjj.ioc.event.executor;

import java.util.concurrent.Executor;

/**
 * Created by GZ1581 on 2016/5/17
 */

public abstract class AbsExecutor implements Executor, IExecutor {
    protected int mId;

    protected AbsExecutor(int id) {
        mId = id;
    }

    public int getExecutorId() {
        return mId;
    }
}
