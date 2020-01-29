package com.payneteasy.swagger.apt;

/**
 * @author dvponomarev, 29.01.2020
 */
public interface IService {

    @ExportToSwagger
    int doSomething(String str);

}
