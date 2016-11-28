package com.ssjj.ioc.event.property;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.List;

/**
 * Created by GZ1581 on 2016/5/19
 */

public final class ListProperty<T> extends Property<List<T>> {

    public ListProperty(List<T> defaultValue) {
        super(defaultValue);
    }

    public void add(int location, T object) {
        synchronized (this) {
            T oldValue = mValue.get(location);
            mValue.add(location, object);

            sendSignal(new ListPropertyUpgrade<>(location, object, oldValue));
        }
    }

    public boolean add(T object) {
        synchronized (this) {
            int index = mValue.size() - 1;
            T oldValue = mValue.get(index);
            mValue.add(object);

            sendSignal(new ListPropertyUpgrade<>(index, object, oldValue));
        }
        return true;
    }

    public boolean contains(T object) {
        synchronized (this) {
            return mValue.contains(object);
        }
    }

    public boolean containsAll(Collection<?> collection) {
        synchronized (this) {
            return mValue.containsAll(collection);
        }
    }

    public T get(int location) {
        synchronized (this) {
            return mValue.get(location);
        }
    }

    public int indexOf(T object) {
        synchronized (this) {
            return mValue.indexOf(object);
        }
    }

    public boolean isEmpty() {
        synchronized (this) {
            return mValue.isEmpty();
        }
    }

    public int lastIndexOf(T object) {
        synchronized (this) {
            return mValue.lastIndexOf(object);
        }
    }

    public T remove(int location) {
        synchronized (this) {
            T removed = mValue.remove(location);
            T newValue = location < mValue.size() ? mValue.get(location) : null;
            sendSignal(new ListPropertyUpgrade<>(location, newValue, removed));
            return removed;
        }
    }

    public boolean remove(T object) {
        synchronized (this) {
            int index = mValue.indexOf(object);
            if (-1 == index) {
                return false;
            }

            boolean ret = mValue.remove(object);
            T newValue = index < mValue.size() ? mValue.get(index) : null;
            sendSignal(new ListPropertyUpgrade<>(index, newValue, object));

            return ret;
        }
    }

    public T set(int location, T object) {
        synchronized (this) {
            T oldValue = mValue.set(location, object);

            sendSignal(new ListPropertyUpgrade<>(location, object, oldValue));

            return oldValue;
        }
    }

    public int size() {
        synchronized (this) {
            return mValue.size();
        }
    }

    @NonNull
    public List<T> subList(int start, int end) {
        synchronized (this) {
            return mValue.subList(start, end);
        }
    }

    @NonNull
    public Object[] toArray() {
        synchronized (this) {
            return mValue.toArray();
        }
    }

    @NonNull
    public T[] toArray(T[] array) {
        synchronized (this) {
            return mValue.toArray(array);
        }
    }
}
