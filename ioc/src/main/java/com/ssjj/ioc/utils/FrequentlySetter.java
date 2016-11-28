package com.ssjj.ioc.utils;

import android.os.*;
import android.os.Process;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by GZ1581 on 2016/6/6
 */
public final class FrequentlySetter {
    private HandlerThread mProgressThread;
    private Handler mProgressHandler;
    private Runnable mProgressRunnable;
    private AtomicBoolean mIsProgressed;
    private AtomicInteger mValue;
    private OnSetListener mListener;

    public FrequentlySetter(OnSetListener l) {
        mListener = l;
        mIsProgressed = new AtomicBoolean(true);
        mValue = new AtomicInteger(0);

        mProgressRunnable = new Runnable() {
            @Override
            public void run() {
                mListener.onValue(mValue.get());

                mIsProgressed.set(true);
            }
        };
    }

    public void start() {
        cancelInner();

        mProgressThread = new HandlerThread("FrequentlySetterThread", Process.THREAD_PRIORITY_DEFAULT);
        mProgressThread.start();
        mProgressHandler = new Handler(mProgressThread.getLooper());

        mListener.onStart();
    }

    public void cancel() {
        cancelInner();

        mListener.onCancel();
    }

    public void setValue(int value) {
        mValue.set(value);

        if (mIsProgressed.get()) {
            mProgressHandler.postDelayed(mProgressRunnable, 300);
            mIsProgressed.set(false);
        }
    }

    private void cancelInner() {
        if (null != mProgressRunnable && null != mProgressHandler) {
            mProgressHandler.removeCallbacks(mProgressRunnable);
        }

        if (null != mProgressThread) {
            mProgressThread.quit();
            mProgressThread = null;
            mProgressHandler = null;
        }

        mIsProgressed.set(true);
    }

    public interface OnSetListener {
        void onStart();

        void onCancel();

        void onValue(int value);
    }
}
