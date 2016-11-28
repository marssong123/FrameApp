package com.ssjj.ioc.event;

import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

/**
 * Created by GZ1581 on 2016/5/17
 */

public final class Slot<T> {

    WeakReference<T> mReceiver;
    Method mMethod;
    boolean mInvokeInMainThread;

    public Slot(T receiver, Method method, boolean invokeInMain) {
        mReceiver = new WeakReference<>(receiver);
        mMethod = method;
        mInvokeInMainThread = invokeInMain;
    }
}
