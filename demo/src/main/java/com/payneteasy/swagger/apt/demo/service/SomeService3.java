package com.payneteasy.swagger.apt.demo.service;

import com.payneteasy.swagger.apt.ExportToSwagger;
import org.springframework.stereotype.Service;

/**
 * This service will not be recognized, interface is required.
 *
 * @author dvponomarev, 28.01.2020
 */
@Service
public class SomeService3 {

    @ExportToSwagger
    public boolean method(boolean a) {
        return true;
    }

}
