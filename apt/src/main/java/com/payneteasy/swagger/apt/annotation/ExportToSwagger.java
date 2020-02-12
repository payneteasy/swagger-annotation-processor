package com.payneteasy.swagger.apt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Export service/method to swagger.<br/>
 * To be set on service <b>interface</b> and/or its methods.<br/>
 * The annotation set on interface exports all its methods.
 * <p/>
 * By default the method name becomes the last path part of the REST method, to override it use {@link #value}.
 *
 * @author dvponomarev, 27.09.2018
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportToSwagger {

    /**
     * Swagger REST method path, defaults to method name.<br/>
     * Usually you should not override default value but this is useful in case of methods overloading.
     * <p/>
     * {@code value()} specified on service interface is ignored.
     */
    String value() default "";

}
