package com.ssjj.ioc.event.property;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * Created by GZ1581 on 2016/5/19
 */

public final class MapProperty<K, V> extends Property<Map<K, V>> {
    public MapProperty(Map<K, V> defaultValue) {
        super(defaultValue);
    }

    public boolean containsKey(K key) {
        synchronized (this) {
            return mValue.containsKey(key);
        }
    }

    public boolean containsValue(V value) {
        synchronized (this) {
            return mValue.containsValue(value);
        }
    }

    public V get(K key) {
        synchronized (this) {
            return mValue.get(key);
        }
    }

    public boolean isEmpty() {
        synchronized (this) {
            return mValue.isEmpty();
        }
    }

    @NonNull
    public Set<K> keySet() {
        synchronized (this) {
            return mValue.keySet();
        }
    }

    public V put(K key, V value) {
        synchronized (this) {
            V old = mValue.put(key, value);

            sendSignal(new MapPropertyUpgrade<>(key, value, old));

            return old;
        }
    }

    public V remove(K key) {
        synchronized (this) {
            V old = mValue.remove(key);

            sendSignal(new MapPropertyUpgrade<>(key, null, old));

            return old;
        }
    }

    public int size() {
        synchronized (this) {
            return mValue.size();
        }
    }

    @NonNull
    public Collection<V> values() {
        synchronized (this) {
            return mValue.values();
        }
    }
}
