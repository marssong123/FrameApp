package com.ssjj.ioc.event.property;

/**
 * Created by GZ1581 on 2016/5/19
 */
public final class ListPropertyUpgrade<T> extends PropertyChanged<T> {
    private int mIndex;

    public ListPropertyUpgrade(int index, T newValue, T oldValue) {
        super(newValue, oldValue);
        mIndex = index;
    }

    public final synchronized int getIndex() {
        return mIndex;
    }
}
