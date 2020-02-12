package com.payneteasy.swagger.apt;

import com.payneteasy.swagger.apt.annotation.ExportToSwagger;

/**
 * This service will not be processed, interface is required.
 *
 * @author dvponomarev, 28.01.2020
 */
public class SomeService3 {

    @ExportToSwagger
    public boolean method(boolean a) {
        return true;
    }

}
