package com.ssjj.ioc.event;

import com.ssjj.ioc.event.annotation.IASlot;
import com.ssjj.ioc.event.executor.AbsExecutor;
import com.ssjj.ioc.event.executor.MainThreadExecutor;
import com.ssjj.ioc.event.executor.SingleThreadExecutor;
import com.ssjj.ioc.event.executor.ThreadPoolExecutor;
import com.ssjj.ioc.event.property.Property;
import com.ssjj.ioc.event.signal.ISignal;
import com.ssjj.ioc.event.signal.SignalType;
import com.ssjj.ioc.log.L;
import com.ssjj.ioc.utils.AdaUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by GZ1581 on 2016/5/17
 */

public final class EventCenter {
    private static final String TAG = "EventCenter";

    static final EventCenter mInstance;

    static {
        mInstance = new EventCenter();
    }

    private Map<Object, List<Slot<?>>> mConnectMap;
    private MainThreadExecutor mMainThread;
    private ThreadPoolExecutor mThreadPool;
    private SingleThreadExecutor mSerialThread;
    private SingleThreadExecutor mSignalHandlerThread;
    private List<IPropertySet> mPropertySet;

    private EventCenter() {
        mPropertySet = new ArrayList<>(1);
        mConnectMap = new HashMap<>();
        mMainThread = new MainThreadExecutor();
        mThreadPool = new ThreadPoolExecutor();
        mSerialThread = new SingleThreadExecutor("SerialThread");
        mSignalHandlerThread = new SingleThreadExecutor("SignalHandlerThread");
    }

    static void init() {

    }

    <T> void sendTo(final Object connectKey, final ISignal signal, final SignalType signalType, final T receiver) {
        switch (signalType) {
            case Sync:
                invoke(connectKey, signal, null, receiver);
                break;
            case AsyncSerial:
                mSignalHandlerThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        invoke(connectKey, signal, mSerialThread, receiver);
                    }
                });
                break;
            case AsyncConcurrent:
            default:
                mSignalHandlerThread.execute(new Runnable() {
                    @Override
                    public void run() {
                        invoke(connectKey, signal, mThreadPool, receiver);
                    }
                });
                break;
        }
    }

    <T> void register(T receiver, String baseClassName) {
        L.info(TAG, "call register receiver %s", receiver.getClass().getName());

        pRegister(receiver, baseClassName);
    }

    <T> void register(T receiver, String methodName, Class<?> signal, boolean invokeInMain, Property<?> property, boolean bindInit) {
        L.info(TAG, "call register receiver %s method %s signal %s invokeInMain %b"
                , receiver.getClass().getName(), methodName, signal.getName(), invokeInMain);

        pRegister(receiver, methodName, signal, invokeInMain, property, bindInit);
    }

    <T> void unRegister(T receiver) {
        L.info(TAG, "call unRegister receiver %s", receiver.getClass().getName());

        pUnRegister(receiver);
    }

    <T> void unRegister(T receiver, String methodName, Class<?> signalClass, Property<?> property) {
        L.info(TAG, "call unRegister receiver %s method %s signal %s", receiver.getClass().getName(), methodName
                , signalClass.getName());

        pUnRegister(receiver, methodName, signalClass, property);
    }

    void registerPropertySet(IPropertySet propertySet) {
        synchronized (this) {
            if (-1 != mPropertySet.indexOf(propertySet)) {
                AdaUtils.crashIfDebug("%s property set %s ready registered", TAG, propertySet.getClass().getName());
                return;
            }

            mPropertySet.add(propertySet);
        }
    }

    private <T> void pRegister(final T receiver, final String baseClassName) {
        mSignalHandlerThread.execute(new Runnable() {
            @Override
            public void run() {
                L.info(TAG, "execute register receiver %s", receiver.getClass().getName());

                Class<?> clz = receiver.getClass();
                while (true) {
                    Method[] methods = clz.getDeclaredMethods();
                    if (null != methods) {
                        for (Method item : methods) {
                            Annotation[] annotations = item.getAnnotations();
                            for (Annotation annotation : annotations) {
                                if (annotation instanceof IASlot) {
                                    Class<?>[] params = item.getParameterTypes();
                                    if (1 != params.length) {
                                        AdaUtils.crashIfDebug("%s register slots but param length not 1, receiver %s, method %s", TAG, receiver.getClass().getName(), item.getName());
                                        continue;
                                    }

                                    IASlot ia = (IASlot) annotation;
                                    Slot<T> slot = new Slot<>(receiver, item, ia.invokeInMain());
                                    connect(params[0], slot);
                                } else {
                                    registerProperty(receiver, item, annotation);
                                }
                            }
                        }
                    }

                    if (0 == baseClassName.compareTo(clz.getName())) {
                        break;
                    }

                    clz = clz.getSuperclass();
                }
            }
        });
    }

    private <T> void pRegister(final T receiver, final String methodName, final Class<?> signal, final boolean invokeInMain, final Property<?> property, final boolean bindInit) {
        mSignalHandlerThread.execute(new Runnable() {
            @Override
            public void run() {
                L.info(TAG, "execute register receiver %s method %s signal %s invokeInMain %b"
                        , receiver.getClass().getName(), methodName, signal.getName(), invokeInMain);

                Method method;
                try {
                    method = receiver.getClass().getDeclaredMethod(methodName, signal);
                } catch (Exception e) {
                    e.printStackTrace();
                    AdaUtils.crashIfDebug("%s, receiver %s not such method %s param %s", TAG, receiver.getClass().getName(), methodName, signal.getName());
                    return;
                }

                Slot<T> slot = new Slot<>(receiver, method, invokeInMain);
                if (null != property) {
                    connect(property, slot);
                    if (bindInit) {
                        property.triggerChangedSignal(receiver);
                    }
                } else {
                    connect(signal, slot);
                }
            }
        });
    }

    private <T> void registerProperty(T receiver, Method method, Annotation annotation) {
        synchronized (this) {
            for (IPropertySet property : mPropertySet) {
                if (property.isProperty(annotation)) {
                    Class<?>[] params = method.getParameterTypes();
                    if (1 != params.length) {
                        AdaUtils.crashIfDebug("%s register property but param length not 1, receiver %s, method %s", TAG, receiver.getClass().getName(), method.getName());
                        continue;
                    }

                    Slot<T> slot = new Slot<>(receiver, method, property.invokeInMain(annotation));
                    connect(property.property(annotation), slot);
                    if (property.bindInit(annotation)) {
                        property.property(annotation).triggerChangedSignal(receiver);
                    }

                    break;
                }
            }
        }
    }

    private <T> void pUnRegister(final T receiver) {
        mSignalHandlerThread.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (EventCenter.this) {
                    L.info(TAG, "execute unRegister receiver %s", receiver.getClass().getName());

                    for (List<Slot<?>> slots : mConnectMap.values()) {
                        Iterator<Slot<?>> iterator = slots.iterator();
                        while (iterator.hasNext()) {
                            Object slotReceiver = iterator.next().mReceiver.get();
                            if (null == slotReceiver || slotReceiver.equals(receiver)) {
                                iterator.remove();
                            }
                        }
                    }
                }
            }
        });
    }

    private <T> void pUnRegister(final T receiver, final String methodName, final Class<?> signalClass, final Property<?> property) {
        mSignalHandlerThread.execute(new Runnable() {
            @Override
            public void run() {
                synchronized (EventCenter.this) {
                    L.info(TAG, "execute unRegister receiver %s method %s signal %s", receiver.getClass().getName(), methodName
                            , signalClass.getName());

                    List<Slot<?>> slots = mConnectMap.get(null == property ? signalClass : property);
                    if (null != slots) {
                        Iterator<Slot<?>> iterator = slots.iterator();
                        while (iterator.hasNext()) {
                            Slot<?> slot = iterator.next();
                            Object slotReceiver = slot.mReceiver.get();
                            if (null == slotReceiver) {
                                iterator.remove();
                            } else if (receiver.equals(slotReceiver) && 0 == methodName.compareTo(slot.mMethod.getName())
                                    && signalClass.equals(slot.mMethod.getParameterTypes()[0])) {
                                iterator.remove();
                                return;
                            }
                        }
                    }
                }
            }
        });
    }

    private void connect(Object signal, Slot<?> slot) {
        synchronized (this) {
            List<Slot<?>> slots = mConnectMap.get(signal);
            if (null == slots) {
                slots = new ArrayList<>();
                mConnectMap.put(signal, slots);
            }

            slots.add(slot);
        }
    }

    private <T> void invoke(Object connectKey, ISignal signal, AbsExecutor executor, T receiver) {
        synchronized (this) {
            List<Slot<?>> slots = mConnectMap.get(connectKey);
            if (null == slots) {
                return;
            }

            for (Slot<?> item : slots) {
                Object slotReceiver = item.mReceiver.get();
                if (null != slotReceiver && (null == receiver || slotReceiver.equals(receiver))) {
                    if (connectKey instanceof Property<?>) {
                        if (signal.getClass().equals(item.mMethod.getParameterTypes()[0])) {
                            invoke(signal, slotReceiver, item.mMethod, item.mInvokeInMainThread ? mMainThread : executor);
                        }
                    } else {
                        invoke(signal, slotReceiver, item.mMethod, item.mInvokeInMainThread ? mMainThread : executor);
                    }
                }
            }
        }
    }

    private <T> void invoke(final ISignal signal, final T receiver, final Method method, AbsExecutor executor) {
        if (null == executor) {
            invoke(signal, receiver, method);
        } else {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    invoke(signal, receiver, method);
                }
            });
        }
    }

    private <T> void invoke(ISignal signal, T receiver, Method method) {
        try {
            method.invoke(receiver, signal);
        } catch (Throwable throwable) {
            AdaUtils.crashIfDebug("EventCenter invoke error %s signal %s receiver %s method %s"
                    , throwable.toString(), signal.getClass().getName()
                    , receiver.getClass().getName(), method.getName());
        }
    }

    /**
     * do not implements or extends EventFriend,
     * it is an inner class
     *
     * @hide
     */
    public static abstract class EventFriend {
        protected <E> void sendSignal(Property<?> property, ISignal signal, SignalType signalType, E receiver) {
            mInstance.sendTo(property, signal, signalType, receiver);
        }

        protected <E> void triggerChangedSignal(E receiver) {
            onTriggerChangedSignal(receiver);
        }

        protected abstract <E> void onTriggerChangedSignal(E receiver);
    }

    public interface IPropertySet {
        boolean isProperty(Annotation annotation);

        boolean invokeInMain(Annotation annotation);

        boolean bindInit(Annotation annotation);

        Property<?> property(Annotation annotation);
    }
}
