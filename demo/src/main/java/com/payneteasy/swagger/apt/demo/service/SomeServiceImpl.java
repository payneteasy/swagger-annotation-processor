package com.payneteasy.swagger.apt.demo.service;

import com.payneteasy.swagger.apt.demo.service.model.ComplexModel;
import com.payneteasy.swagger.apt.demo.service.model.EnumModel;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author dvponomarev, 28.01.2020
 */
@Service
public class SomeServiceImpl implements ISomeService {

    @Override
    public void empty() {
    }

    @Override
    public void noArguments() {
    }

    @Override
    public String noArguments2() {
        return "noArguments2 method return";
    }

    @Override
    public long manyArguments(int anInt1, String aString1) {
        return 999L;
    }

    @Override
    public String manyArgumentsEnum(EnumModel aEnum2, int anInt2, String aString2) {
        return String.format("manyArgumentsEnum method return for arguments: %s, %d, %s", aEnum2, anInt2, aString2);
    }

    @Override
    public ComplexModel oneComplexArgument(ComplexModel aModel3) {
        final ComplexModel complexModel = new ComplexModel();
        complexModel.text   = "oneComplexArgument method";
        complexModel.status = 777;
        return complexModel;
    }

    @Override
    public void manyArgumentsComplexOne(ComplexModel aModel4, String aString4) {
    }

    @Override
    public List<ComplexModel> parameterizedOne(List<String> strings5) {
        final ComplexModel complexModel1 = new ComplexModel();
        complexModel1.text   = "parameterizedOne method";
        complexModel1.status = 777;
        final ComplexModel complexModel2 = new ComplexModel();
        complexModel2.text   = "parameterizedOne method 2";
        complexModel2.status = 888;
        return Arrays.asList(complexModel1, complexModel2);
    }

    @Override
    public void variableArguments(int anInt6) {
    }

    @Override
    public void variableArguments(int anInt7, ComplexModel aModel7) {
    }

    @Override
    public void variableArguments(String aString8) {
    }

}
