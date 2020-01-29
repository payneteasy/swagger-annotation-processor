package com.payneteasy.swagger.apt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Export method to swagger.<br/>
 * To be set on service <b>interface</b> methods only.<br/>
 * By default the method name becomes the last path part on the REST method, to override it use {@link #value}.
 *
 * @author dvponomarev, 27.09.2018
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ExportToSwagger {

    /**
     * Swagger REST method path, defaults to method name.<br/>
     * Usually you should not override default value but this is useful in case of methods overloading.
     *
     * @see MethodId
     */
    String value() default "";

}
