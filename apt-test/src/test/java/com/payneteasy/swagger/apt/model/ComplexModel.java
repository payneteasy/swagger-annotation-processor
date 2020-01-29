package com.payneteasy.swagger.apt.model;

/**
 * @author dvponomarev, 27.09.2018
 */
public class ComplexModel {

    public int status;

    public String text;

    @Override
    public String toString() {
        return "ComplexModel{" +
               "status=" + status +
               ", text='" + text + '\'' +
               '}';
    }

}
