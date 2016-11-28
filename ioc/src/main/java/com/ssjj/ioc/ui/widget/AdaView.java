package com.ssjj.ioc.ui.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.Animation;

import com.ssjj.ioc.log.L;
import com.ssjj.ioc.utils.ResourceUtils;

import java.lang.ref.WeakReference;

/**
 * Created by GZ1581 on 2016/5/16
 */
public class AdaView<T extends View> {
    private WeakReference<Object> mRoot;
    private T mView;
    private int mViewId;
    private String mViewIdName;
    protected static final String TAG = "AdaView";

    public AdaView(Object root, int id) {
        mRoot = new WeakReference<>(root);
        mViewId = id;
    }

    public AdaView(Object root, String idName) {
        mRoot = new WeakReference<>(root);
        mViewId = -1;
        mViewIdName = idName;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        if (null != mView) {
            return mView;
        }

        try {
            Object root = mRoot.get();
            if (root instanceof View) {
                View rootView = (View) root;
                if (-1 == mViewId) {
                    mViewId = ResourceUtils.getIdByIdName(rootView.getContext(), mViewIdName);
                }

                mView = (T) rootView.findViewById(mViewId);
            } else if (root instanceof AdaActivity) {
                AdaActivity rootActivity = (AdaActivity) root;
                if (-1 == mViewId) {
                    mViewId = ResourceUtils.getIdByIdName(rootActivity, mViewIdName);
                }

                mView = (T) rootActivity.findViewById(mViewId);
            }
        } catch (Throwable throwable) {
            mView = null;
            L.error(TAG, throwable.toString());
        }

        return mView;
    }

    public void setOnClickListener(View.OnClickListener l) {
        get().setOnClickListener(l);
    }

    public void setOnTouchListener(View.OnTouchListener l) {
        get().setOnTouchListener(l);
    }

    public void setVisibility(int visibility) {
        get().setVisibility(visibility);
    }

    public int getVisibility() {
        return get().getVisibility();
    }

    public void setSelected(boolean selected) {
        get().setSelected(selected);
    }

    public boolean isSelected() {
        return get().isSelected();
    }

    public void setBackground(Drawable background) {
        get().setBackground(background);
    }

    public void setBackgroundColor(int color) {
        get().setBackgroundColor(color);
    }

    public void setBackgroundResource(int resid) {
        get().setBackgroundResource(resid);
    }

    public Drawable getBackground() {
        return get().getBackground();
    }

    public void setEnabled(boolean enabled) {
        get().setEnabled(enabled);
    }

    public boolean isEnabled() {
        return get().isEnabled();
    }

    public void setAnimation(Animation animation) {
        get().setAnimation(animation);
    }

    public Animation getAnimation() {
        return get().getAnimation();
    }

    public void clearAnimation() {
        get().clearAnimation();
    }

    public void startAnimation(Animation animation) {
        get().startAnimation(animation);
    }

    public ViewPropertyAnimator animate() {
        return get().animate();
    }

    public void setClickable(boolean clickable) {
        get().setClickable(clickable);
    }

    public boolean isClickable() {
        return get().isClickable();
    }

    public Context getContext() {
        return get().getContext();
    }
}
