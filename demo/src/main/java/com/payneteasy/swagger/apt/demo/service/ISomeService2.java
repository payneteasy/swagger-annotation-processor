package com.payneteasy.swagger.apt.demo.service;

import com.payneteasy.swagger.apt.annotation.ExportToSwagger;

/**
 * @author dvponomarev, 27.09.2018
 */
@ExportToSwagger
public interface ISomeService2 {

    /** Empty method with duplicate annotation. */
    @ExportToSwagger
    void empty();

    void singleArgument(int anInt);

}
