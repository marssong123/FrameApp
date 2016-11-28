package com.ssjj.ioc.ui.widget;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ssjj.ioc.event.EventCenterProxy;
import com.ssjj.ioc.ui.annotation.IAFragment;
import com.ssjj.ioc.log.UILog;

/**
 * Created by GZ1581 on 2016/5/16.
 */
public class AdaDialogFragment extends DialogFragment {
    private static final String BaseClassName = AdaDialogFragment.class.getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        UILog.lifeCycle(this, "onCreate");

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        UILog.lifeCycle(this, "onCreateView");

        View view = null;
        Class<?> clz = getClass();
        IAFragment ia = clz.getAnnotation(IAFragment.class);
        if (null != ia) {
            view = inflater.inflate(ia.value(), container, false);
            IAHolderBinder.bind(this, view, BaseClassName);
        }

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        UILog.lifeCycle(this, "onActivityCreated");

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        UILog.lifeCycle(this, "onAttach context");

        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        UILog.lifeCycle(this, "onDetach");

        super.onDetach();
    }

    @Override
    public void onStart() {
        UILog.lifeCycle(this, "onStart");

        super.onStart();
    }

    @Override
    public void onStop() {
        UILog.lifeCycle(this, "onStop");

        super.onStop();
    }

    @Override
    public void onResume() {
        UILog.lifeCycle(this, "onResume");

        super.onResume();

        EventCenterProxy.register(this, BaseClassName);
    }

    @Override
    public void onPause() {
        UILog.lifeCycle(this, "onPause");

        super.onPause();

        EventCenterProxy.unRegister(this);
    }

    @Override
    public void onDestroyView() {
        UILog.lifeCycle(this, "onDestroyView");

        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        UILog.lifeCycle(this, "onDestroy");

        super.onDestroy();
    }
}
