package com.ssjj.ioc.event.property;

import com.ssjj.ioc.event.EventCenter;
import com.ssjj.ioc.event.EventCenterProxy;
import com.ssjj.ioc.event.signal.SignalType;

/**
 * Created by GZ1581 on 2016/5/18
 */

public class Property<T> extends EventCenter.EventFriend {

    protected T mDefaultValue;
    protected T mValue;

    public Property(T defaultValue) {
        synchronized (this) {
            mDefaultValue = defaultValue;
            mValue = defaultValue;
        }
    }

    public final synchronized T get() {
        return mValue;
    }

    public final synchronized void set(T value) {
        if (isEquals(mValue, value)) {
            return;
        }

        T oldValue = mValue;
        mValue = value;
        sendSignal(new PropertyChanged<>(mValue, oldValue));
    }

    public final synchronized T getDefaultValue() {
        return mDefaultValue;
    }

    public synchronized boolean isDefault() {
        return isEquals(mValue, mDefaultValue);
    }

    public synchronized void reset() {
        set(mDefaultValue);
    }

    public synchronized <E> void connect(E receiver, String method, boolean invokeInMain, boolean bindInit) {
        EventCenterProxy.register(receiver, method, PropertyChanged.class, invokeInMain, this, bindInit);
    }

    public synchronized <E> void disConnect(E receiver, String method) {
        EventCenterProxy.unRegister(receiver, method, PropertyChanged.class, this);
    }

    private boolean isEquals(T oldValue, T newValue) {
        return (null == oldValue && null == newValue) || (null != oldValue && oldValue.equals(newValue));
    }

    protected final void sendSignal(PropertyChanged<?> propertyChanged) {
        sendSignal(this, propertyChanged, SignalType.AsyncSerial, null);
    }

    @Override
    protected <E> void onTriggerChangedSignal(E receiver) {
        sendSignal(this, new PropertyChanged<>(mValue, mValue), SignalType.AsyncSerial, receiver);
    }
}
