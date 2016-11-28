package com.ssjj.ioc.event.executor;

/**
 * Created by GZ1581 on 2016/5/17
 */
public interface IExecutor {
    int Sync = 0;
    int SingleThread = 1;
    int ThreadPool = 2;
    int MainThread = 3;
}
