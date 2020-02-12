package com.payneteasy.swagger.apt;

/**
 * @author dvponomarev, 27.09.2018
 */
@ExportToSwagger
public interface ISomeService2 {

    @ExportToSwagger
    void empty();

    void singleArgument(int anInt);

}
