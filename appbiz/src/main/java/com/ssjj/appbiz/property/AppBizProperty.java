package com.ssjj.appbiz.property;

import com.ssjj.ioc.event.EventCenter;
import com.ssjj.ioc.event.EventCenterProxy;
import com.ssjj.ioc.event.property.Property;
import com.ssjj.ioc.property.IProperty;

import java.lang.annotation.Annotation;

/**
 * Created by GZ1581 on 2016/5/23
 */

public enum AppBizProperty implements IProperty {
    ;

    AppBizProperty(Property<?> p) {
        mPtr = p;
    }

    @Override
    public Property<?> value() {
        return mPtr;
    }

    private Property<?> mPtr;

    static {
        EventCenterProxy.registerPropertySet(new PropertySet());
    }

    private static final class PropertySet implements EventCenter.IPropertySet {

        @Override
        public boolean isProperty(Annotation annotation) {
            return annotation instanceof IAAppBizProperty;
        }

        @Override
        public boolean invokeInMain(Annotation annotation) {
            return ((IAAppBizProperty) annotation).invokeInMain();
        }

        @Override
        public boolean bindInit(Annotation annotation) {
            return ((IAAppBizProperty) annotation).bindInit();
        }

        @Override
        public Property<?> property(Annotation annotation) {
            return ((IAAppBizProperty) annotation).value().value();
        }
    }
}
