package com.ssjj.ioc.property;

import com.ssjj.ioc.event.property.Property;
import com.ssjj.ioc.module.network.NetWorkModule;

/**
 * Created by GZ1581 on 2016/5/27
 */

public interface AdaProperties {
    Property<NetWorkModule.NetType> NetWorkType = new Property<>(NetWorkModule.NetType.None);
    Property<Boolean> NetWorkAvailable = new Property<>(true);
}
