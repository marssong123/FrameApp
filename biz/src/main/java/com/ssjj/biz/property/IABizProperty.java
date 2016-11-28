package com.ssjj.biz.property;


import com.ssjj.ioc.property.IProperty;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by GZ1581 on 2016/5/27
 */

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IABizProperty {
    boolean invokeInMain() default IProperty.InvokeInMain;

    boolean bindInit() default IProperty.BindInit;

    BizProperty value();
}
