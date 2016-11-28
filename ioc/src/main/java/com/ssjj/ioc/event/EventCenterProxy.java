package com.ssjj.ioc.event;

import com.ssjj.ioc.event.property.Property;
import com.ssjj.ioc.event.property.PropertyChanged;
import com.ssjj.ioc.event.signal.EventSignal;
import com.ssjj.ioc.event.signal.SignalType;

/**
 * Created by GZ1581 on 2016/5/17
 */
public final class EventCenterProxy {

    public static void init() {
        EventCenter.init();
    }

    public static <T> void register(T receiver, String baseClassName) {
        EventCenter.mInstance.register(receiver, baseClassName);
    }

    public static <T, E extends EventSignal> void register(T receiver, String methodName, Class<E> eventSignal, boolean invokeInMain) {
        EventCenter.mInstance.register(receiver, methodName, eventSignal, invokeInMain, null, false);
    }

    public static <T, E extends PropertyChanged<?>>
    void register(T receiver, String methodName, Class<E> methodParam, boolean invokeInMain, Property<?> property, boolean bindInit) {
        EventCenter.mInstance.register(receiver, methodName, methodParam, invokeInMain, property, bindInit);
    }

    public static <T> void unRegister(T receiver) {
        EventCenter.mInstance.unRegister(receiver);
    }

    public static <T, E extends EventSignal> void unRegister(T receiver, String methodName, Class<E> eventSignal) {
        EventCenter.mInstance.unRegister(receiver, methodName, eventSignal, null);
    }

    public static <T> void unRegister(T receiver, String methodName, Class<?> methodParam, Property<?> property) {
        EventCenter.mInstance.unRegister(receiver, methodName, methodParam, property);
    }

    public static void send(EventSignal signal) {
        EventCenter.mInstance.sendTo(signal.getClass(), signal, SignalType.AsyncConcurrent, null);
    }

    public static void send(EventSignal signal, SignalType signalType) {
        EventCenter.mInstance.sendTo(signal.getClass(), signal, signalType, null);
    }

    public static void registerPropertySet(EventCenter.IPropertySet propertySet) {
        EventCenter.mInstance.registerPropertySet(propertySet);
    }
}
