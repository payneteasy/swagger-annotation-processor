package com.payneteasy.swagger.apt;

import com.payneteasy.swagger.apt.annotation.ExportToSwagger;
import com.payneteasy.swagger.apt.annotation.MethodParam;

/**
 * @author dvponomarev, 27.09.2018
 */
@ExportToSwagger
public interface ISomeService2 {

    @ExportToSwagger
    void empty();

    void singleArgument(int anInt);

    void parametersNamesOverrides(@MethodParam("anInt2") int anInt, @MethodParam("aString2") String aString);

}
