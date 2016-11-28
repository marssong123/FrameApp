package com.ssjj.ioc.ui.widget;

import android.support.v4.app.Fragment;
import android.view.View;

import com.ssjj.ioc.log.L;
import com.ssjj.ioc.ui.annotation.IAFragment;
import com.ssjj.ioc.ui.annotation.IAView;

import java.lang.reflect.Field;

/**
 * Created by GZ1581 on 2016/5/16
 */
public final class IAHolderBinder {
    private static final char IdNameSeparator = '_';

    public static void bind(AdaActivity activity, String baseClassName) {
        Class<?> clz = activity.getClass();
        while (true) {
            initFields(activity, clz);

            if (0 == baseClassName.compareTo(clz.getName())) {
                break;
            }

            clz = clz.getSuperclass();
        }
    }

    public static void bind(AdaFragment fragment, View root, String baseClassName) {
        bindFragment(fragment, root, baseClassName);
    }

    public static void bind(AdaDialogFragment dialogFragment, View root, String baseClassName) {
        bindFragment(dialogFragment, root, baseClassName);
    }

    private static void bindFragment(Fragment fragment, View root, String baseClassName) {
        Class<?> clz = fragment.getClass();
        while (true) {
            initFields(fragment, root, clz);

            if (0 == baseClassName.compareTo(clz.getName())) {
                break;
            }

            clz = clz.getSuperclass();
        }
    }

    public static void bind(IAdaView view) {
        Class<?> clz = view.getClass();
        while (clz.getName().startsWith(IAdaView.BasePackageName)) {
            initFields((View) view, clz);
            clz = clz.getSuperclass();
        }
    }

    private static void initFields(AdaActivity activity, Class<?> clz) {
        Field[] fields = clz.getDeclaredFields();
        if (null == fields) {
            return;
        }

        for (Field item : fields) {
            if (AdaView.class.isAssignableFrom(item.getType())) {
                initAdaViewField(activity, activity, item);
            } else if (AdaFragmentHolder.class.isAssignableFrom(item.getType())) {
                initFragmentHolderField(activity, activity, item);
            }
        }
    }

    private static void initFields(Fragment fragment, View root, Class<?> clz) {
        Field[] fields = clz.getDeclaredFields();
        if (null == fields) {
            return;
        }

        for (Field item : fields) {
            if (AdaView.class.isAssignableFrom(item.getType())) {
                initAdaViewField(root, fragment, item);
            } else if (AdaFragmentHolder.class.isAssignableFrom(item.getType())) {
                initFragmentHolderField(fragment, fragment, item);
            }
        }
    }

    private static void initFields(View view, Class<?> clz) {
        Field[] fields = clz.getDeclaredFields();
        if (null == fields) {
            return;
        }

        for (Field item : fields) {
            if (AdaView.class.isAssignableFrom(item.getType())) {
                initAdaViewField(view, view, item);
            } else if (AdaFragmentHolder.class.isAssignableFrom(item.getType())) {
                initFragmentHolderField(view, view, item);
            }
        }
    }

    private static void initAdaViewField(Object root, Object object, Field field) {
        IAView ia = field.getAnnotation(IAView.class);

        AdaView<View> view;
        if (null == ia || 0 == ia.value()) {
            view = new AdaView<>(root, toIdName(field.getName()));
        } else {
            view = new AdaView<>(root, ia.value());
        }

        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(object, view);
        } catch (Exception e) {
            L.error(object, "init AdaView error %s", e.toString());
        }

        field.setAccessible(accessible);
    }

    private static void initFragmentHolderField(Object root, Object object, Field field) {
        IAFragment ia = field.getAnnotation(IAFragment.class);

        AdaFragmentHolder<Fragment> fragment;
        if (null == ia || 0 == ia.value()) {
            fragment = new AdaFragmentHolder<>(root, toIdName(field.getName()));
        } else {
            fragment = new AdaFragmentHolder<>(root, ia.value());
        }

        boolean accessible = field.isAccessible();
        field.setAccessible(true);
        try {
            field.set(object, fragment);
        } catch (Exception e) {
            L.error(object, "init AdaFragmentHolder error %s", e.toString());
        }

        field.setAccessible(accessible);
    }

    private static String toIdName(String fieldName) {
        StringBuilder idName = new StringBuilder();
        idName.append(Character.toLowerCase(fieldName.charAt(1)));

        char index;
        for (int i = 2; i < fieldName.length(); ++i) {
            index = fieldName.charAt(i);
            if (Character.isUpperCase(index)) {
                idName.append(IdNameSeparator);
                idName.append(Character.toLowerCase(index));
            } else {
                idName.append(index);
            }
        }

        return idName.toString();
    }
}
