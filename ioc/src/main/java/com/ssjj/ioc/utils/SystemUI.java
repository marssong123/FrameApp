package com.ssjj.ioc.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.WindowManager;

import com.ssjj.ioc.application.IocApplication;
import com.ssjj.ioc.log.L;

import java.lang.reflect.Method;

/**
 * Created by GZ1581 on 2016/5/25
 */
public final class SystemUI {
    public static final int NavigationBarUnknown = 0;
    public static final int NavigationBarRight = 1;
    public static final int NavigationBarBottom = 2;
    protected static final String TAG = "SystemUI";

    public static boolean hasNavigationBar(Context context) {
        if (null == context) {
            return true;
        }

        boolean hasMenuKey = ViewConfiguration.get(context).hasPermanentMenuKey();
        boolean hasHomeKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_HOME);

        return (!hasMenuKey && !hasHomeKey);
    }

    public static int getStatusBarHeight() {
        Resources res = IocApplication.gContext.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        return (0 < resourceId ? res.getDimensionPixelSize(resourceId) : -1);
    }

    public static int getNavigationBarHeight() {
        Resources res = IocApplication.gContext.getResources();
        int resId = res.getIdentifier("navigation_bar_height", "dimen", "android");
        return (0 < resId ? res.getDimensionPixelSize(resId) : -1);
    }

    public static int getNavigationBarLandscapeHeight() {
        Resources res = IocApplication.gContext.getResources();
        int resId = res.getIdentifier("navigation_bar_height_landscape", "dimen", "android");
        return (0 < resId ? res.getDimensionPixelSize(resId) : -1);
    }

    public static int getNavigationBarLandscapeWidth() {
        Resources res = IocApplication.gContext.getResources();
        int resId = res.getIdentifier("navigation_bar_width", "dimen", "android");
        return (0 < resId ? res.getDimensionPixelSize(resId) : -1);
    }

    //warn this function does not work when keyboard is visible
    public static int getNavigationBarLocal(Activity activity) {
        if (null == activity) {
            return NavigationBarUnknown;
        }

        Rect visibleFrame = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(visibleFrame);

        int screenW = getScreenRealWidth(activity);
        int screenH = getScreenRealHeight(activity);

        if (-1 == screenH || -1 == screenW) {
            return NavigationBarUnknown;
        }

        if (screenH == visibleFrame.bottom) {
            return NavigationBarRight;
        } else if (screenW == visibleFrame.right) {
            return NavigationBarBottom;
        }

        return NavigationBarUnknown;
    }

    public static void setNavigationBarVisible(View view, boolean visible) {
        if (null == view || null == view.getRootView()) {
            return;
        }

        if (!hasNavigationBar(view.getContext())) {
            return;
        }

        if (apiLevelLowerThan16()) {
            view.getRootView().setSystemUiVisibility(visible ? View.SYSTEM_UI_FLAG_VISIBLE : View.SYSTEM_UI_FLAG_LOW_PROFILE);
            return;
        }

        int flag = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION & (~View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        if (!visible) {
            flag = View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                flag |= View.SYSTEM_UI_FLAG_IMMERSIVE;
            }
        }

        view.getRootView().setSystemUiVisibility(flag);
    }

    public static void hideNavigationBarLayout(View view) {
        if (apiLevelLowerThan16()) {//if api level lower than 16 we use SYSTEM_UI_FLAG_LOW_PROFILE in setNavigationBarVisible
            return;
        }

        if (null == view || null == view.getRootView()) {
            return;
        }

        if (!hasNavigationBar(view.getContext())) {
            return;
        }

        view.getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    public static void hideNavigationBar(View view) {
        if (apiLevelLowerThan16()) {//if api level lower than 16 we use SYSTEM_UI_FLAG_LOW_PROFILE in setNavigationBarVisible
            return;
        }

        if (null == view || null == view.getRootView()) {
            return;
        }

        int flag = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            flag |= View.SYSTEM_UI_FLAG_IMMERSIVE;
        }

        view.getRootView().setSystemUiVisibility(flag);
    }

    public static void showSystemUIVisible(View view) {
        if (null == view || null == view.getRootView()) {
            return;
        }
        view.getRootView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }

    //compatibility lower api, such as api 14
    public static void requestStatueBarVisible(Window window, boolean visible) {
        WindowManager.LayoutParams attrs = window.getAttributes();
        if (visible) {
            attrs.flags &= ~WindowManager.LayoutParams.FLAG_FULLSCREEN;
        } else {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        }

        window.setAttributes(attrs);
    }

    public static int getScreenRealHeight(Activity activity) {
        if (null == activity) {
            return -1;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Point sz = getScreenRealSizeAPI17(activity);
            return sz.y;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return getRawSize(activity, "getRawHeight", "getScreenRealHeight");
        }

        return getScreenSizeAPILower(activity, true);
    }

    public static int getScreenRealWidth(Activity activity) {
        if (null == activity) {
            return -1;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Point sz = getScreenRealSizeAPI17(activity);
            return sz.x;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return getRawSize(activity, "getRawWidth", "getScreenRealWidth");
        }

        return getScreenSizeAPILower(activity, false);
    }

    public static boolean hideNavigationWithLowProfile() {
        return apiLevelLowerThan16();
    }

    private static Point getScreenRealSizeAPI17(Activity activity) {
        Point sz = new Point();
        WindowManager wm = activity.getWindowManager();
        Display display = wm.getDefaultDisplay();
        display.getRealSize(sz);
        return sz;
    }

    private static int getRawSize(Activity activity, String name, String exceptionTag) {
        try {
            Method mGetRawH = Display.class.getMethod(name);
            WindowManager wm = activity.getWindowManager();
            Display display = wm.getDefaultDisplay();
            return (Integer) mGetRawH.invoke(display);
        } catch (Exception e) {
            L.error(TAG, "systemUI %s exception %s", exceptionTag, e.toString());
            return -1;
        }
    }

    private static int getScreenSizeAPILower(Activity activity, boolean heightOrWidth) {
        WindowManager wm = activity.getWindowManager();
        Display display = wm.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        return (heightOrWidth ? dm.heightPixels : dm.widthPixels);
    }

    private static boolean apiLevelLowerThan16() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN;
    }
}
