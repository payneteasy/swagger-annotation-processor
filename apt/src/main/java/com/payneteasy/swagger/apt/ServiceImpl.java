package com.payneteasy.swagger.apt;

/**
 * @author dvponomarev, 29.01.2020
 */
public class ServiceImpl implements IService {
    @Override
    public int doSomething(String str) {
        return 777;
    }
}
