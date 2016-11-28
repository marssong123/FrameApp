package com.ssjj.ioc.property;

import com.ssjj.ioc.event.property.Property;

/**
 * Created by GZ1581 on 2016/5/27
 */

public interface IProperty {
    boolean InvokeInMain = false;
    boolean BindInit = true;

    Property<?> value();
}
