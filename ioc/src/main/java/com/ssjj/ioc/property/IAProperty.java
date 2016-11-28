package com.ssjj.ioc.property;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by GZ1581 on 2016/5/26
 */

@Inherited
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IAProperty {
    boolean invokeInMain() default IProperty.InvokeInMain;

    boolean bindInit() default IProperty.BindInit;

    AdaProperty value();
}
