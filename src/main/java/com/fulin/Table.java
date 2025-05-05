package com.fulin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @Author: Fulin
 * @Description: 表名注解
 * @DateTime: 2025/5/5 上午9:49
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Table {
    String tableName();
}
