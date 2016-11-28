package com.ssjj.ioc.ui.widget;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.ssjj.ioc.event.EventCenterProxy;
import com.ssjj.ioc.ui.annotation.IAActivity;
import com.ssjj.ioc.log.UILog;

/**
 * Created by GZ1581 on 2016/5/16
 */
public class AdaActivity extends FragmentActivity {
    private static final String BaseClassName = AdaActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        UILog.lifeCycle(this, "onCreate");

        super.onCreate(savedInstanceState);

        Class<?> clz = getClass();
        IAActivity ia = clz.getAnnotation(IAActivity.class);

        if (null != ia) {
            setContentView(ia.value());
            IAHolderBinder.bind(this, BaseClassName);
        }
    }

    @Override
    protected void onStart() {
        UILog.lifeCycle(this, "onStart");

        super.onStart();
    }

    @Override
    protected void onStop() {
        UILog.lifeCycle(this, "onStop");

        super.onStop();
    }

    @Override
    protected void onRestart() {
        UILog.lifeCycle(this, "onRestart");

        super.onRestart();
    }

    @Override
    protected void onResume() {
        UILog.lifeCycle(this, "onResume");

        super.onResume();

        EventCenterProxy.register(this, BaseClassName);
    }

    @Override
    protected void onPause() {
        UILog.lifeCycle(this, "onPause");

        super.onPause();

        EventCenterProxy.unRegister(this);
    }

    @Override
    protected void onDestroy() {
        UILog.lifeCycle(this, "onDestroy");

        super.onDestroy();
    }

    @Override
    public void onAttachedToWindow() {
        UILog.lifeCycle(this, "onAttachedToWindow");

        super.onAttachedToWindow();
    }

    @Override
    public void onDetachedFromWindow() {
        UILog.lifeCycle(this, "onDetachedFromWindow");

        super.onDetachedFromWindow();
    }
}
