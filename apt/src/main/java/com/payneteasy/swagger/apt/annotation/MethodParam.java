package com.payneteasy.swagger.apt.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Service method parameter name override.<br/>
 * It is used for service survive on service method parameters rename.
 *
 * @author dvponomarev, 11.02.2020
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.CLASS)
public @interface MethodParam {

    /** Service method parameter name override. */
    String value();

}
