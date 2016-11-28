package com.ssjj.ioc.ui.widget;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.View;

import com.ssjj.ioc.log.L;
import com.ssjj.ioc.utils.ResourceUtils;

import java.lang.ref.WeakReference;

/**
 * Created by GZ1581 on 2016/5/16
 */

public class AdaFragmentHolder<T extends Fragment> {

    private WeakReference<Object> mRoot;
    private T mFragment;
    private int mFragmentId;
    private String mFragmentIdName;

    public AdaFragmentHolder(Object root, int id) {
        mRoot = new WeakReference<>(root);
        mFragmentId = id;
    }

    public AdaFragmentHolder(Object root, String idName) {
        mRoot = new WeakReference<>(root);
        mFragmentId = -1;
        mFragmentIdName = idName;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        if (null != mFragment) {
            return mFragment;
        }

        try {
            Object root = mRoot.get();
            if (root instanceof AdaActivity) {
                AdaActivity fa = (AdaActivity) root;
                if (-1 == mFragmentId) {
                    mFragmentId = ResourceUtils.getIdByIdName(fa, mFragmentIdName);
                }

                mFragment = (T) fa.getSupportFragmentManager().findFragmentById(mFragmentId);
            } else if (root instanceof Fragment) {
                Fragment fragment = (Fragment) root;
                if (-1 == mFragmentId) {
                    mFragmentId = ResourceUtils.getIdByIdName(fragment.getContext(), mFragmentIdName);
                }

                mFragment = (T) fragment.getFragmentManager().findFragmentById(mFragmentId);
            } else if (root instanceof View) {
                Context context = ((View) root).getContext();
                if (context instanceof AdaActivity) {
                    AdaActivity fa = (AdaActivity) context;
                    if (-1 == mFragmentId) {
                        mFragmentId = ResourceUtils.getIdByIdName(context, mFragmentIdName);
                    }

                    mFragment = (T) fa.getSupportFragmentManager().findFragmentById(mFragmentId);
                }
            }
        } catch (Throwable throwable) {
            mFragment = null;
            L.error("AdaFragmentHolder", throwable.toString());
        }

        return mFragment;
    }
}
