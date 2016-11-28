package com.ssjj.ioc.property;

import com.ssjj.ioc.event.EventCenter;
import com.ssjj.ioc.event.EventCenterProxy;
import com.ssjj.ioc.event.property.Property;

import java.lang.annotation.Annotation;

/**
 * Created by GZ1581 on 2016/5/23
 */

public enum AdaProperty implements IProperty {
    NetWorkType(AdaProperties.NetWorkType),
    NetWorkAvailable(AdaProperties.NetWorkAvailable);

    AdaProperty(Property<?> p) {
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
            return annotation instanceof IAProperty;
        }

        @Override
        public boolean invokeInMain(Annotation annotation) {
            return ((IAProperty) annotation).invokeInMain();
        }

        @Override
        public boolean bindInit(Annotation annotation) {
            return ((IAProperty) annotation).bindInit();
        }

        @Override
        public Property<?> property(Annotation annotation) {
            return ((IAProperty) annotation).value().value();
        }
    }
}
