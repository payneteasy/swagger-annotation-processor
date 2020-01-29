package com.payneteasy.swagger.apt.demo.spring;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author dvponomarev, 18.12.2019
 */
@Configuration
@ComponentScan(
        basePackages = {
                "com.payneteasy.swagger.apt.demo.controller",
                "com.payneteasy.swagger.apt.demo.service"
        }
)
public class AppConfig {

}
