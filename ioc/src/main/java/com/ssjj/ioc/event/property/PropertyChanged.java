package com.ssjj.ioc.event.property;


import com.ssjj.ioc.event.signal.ISignal;

/**
 * Created by GZ1581 on 2016/5/19
 */

public class PropertyChanged<T> implements ISignal {
    private T mNewValue;
    private T mOldValue;

    public PropertyChanged(T newValue, T oldValue) {
        synchronized (this) {
            mNewValue = newValue;
            mOldValue = oldValue;
        }
    }

    public final synchronized T getOldValue() {
        return mOldValue;
    }

    public final synchronized T getNewValue() {
        return mNewValue;
    }
}
