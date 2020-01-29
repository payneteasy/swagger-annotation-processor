package com.payneteasy.swagger.apt.gen;

import com.payneteasy.swagger.apt.MethodMeta;

import java.lang.reflect.Method;

public class ServiceMethodInfo {

    public final Method     method;
    public final MethodMeta methodMeta;

    ServiceMethodInfo(Method method, MethodMeta methodMeta) {
        this.method     = method;
        this.methodMeta = methodMeta;
    }

}
