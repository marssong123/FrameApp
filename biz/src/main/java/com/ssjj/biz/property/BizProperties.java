package com.ssjj.biz.property;

import com.ssjj.ioc.event.property.Property;
import com.ssjj.biz.module.login.LoginModule;

/**
 * Created by GZ1581 on 2016/5/27
 */

public interface BizProperties {
    Property<LoginModule.LoginStatus> LoginStatus = new Property<>(LoginModule.LoginStatus.Unknown);
    Property<String> LoginToken = new Property<>("");
}
