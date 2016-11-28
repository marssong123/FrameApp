package com.ssjj.ioc.event;

import com.ssjj.ioc.event.executor.IExecutor;

/**
 * Created by GZ1581 on 2016/5/17
 */

public interface SlotType {
    int Sync = IExecutor.Sync;
    int Async = IExecutor.ThreadPool;
    int MainThread = IExecutor.MainThread;
}
