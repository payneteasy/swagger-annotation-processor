package com.payneteasy.swagger.apt.demo.service.model;

/**
 * @author dvponomarev, 18.01.2019
 */
public enum EnumModel {

    A("a"),
    B("b"),
    C("c");

    public final String someInternalValue;

    EnumModel(String someInternalValue) {
        this.someInternalValue = someInternalValue;
    }

}
