package com.ssjj.ioc.event.property;

/**
 * Created by GZ1581 on 2016/5/19
 */

public final class MapPropertyUpgrade<K, V> extends PropertyChanged<V> {
    private K mKey;

    public MapPropertyUpgrade(K key, V newValue, V oldValue) {
        super(newValue, oldValue);
        mKey = key;
    }

    public final synchronized K getKey() {
        return mKey;
    }
}
