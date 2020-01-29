package com.payneteasy.swagger.apt;

import com.payneteasy.swagger.apt.model.ComplexModel;
import com.payneteasy.swagger.apt.model.EnumModel;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author dvponomarev, 24.01.2019
 */
public class SomeServiceMetaTestImpl implements ISomeService {

    public Method method;

    @Override
    public void empty() {
        method = new Object() {}.getClass().getEnclosingMethod();
    }

    @Override
    public void noArguments() {
        method = new Object() {}.getClass().getEnclosingMethod();
    }

    @Override
    public String noArguments2() {
        method = new Object() {}.getClass().getEnclosingMethod();
        return null;
    }

    @Override
    public long manyArguments(int anInt1, String aString1) {
        method = new Object() {}.getClass().getEnclosingMethod();
        return 0;
    }

    @Override
    public String manyArgumentsEnum(EnumModel aEnum2, int anInt2, String aString2) {
        method = new Object() {}.getClass().getEnclosingMethod();
        return null;
    }

    @Override
    public ComplexModel oneComplexArgument(ComplexModel aModel3) {
        method = new Object() {}.getClass().getEnclosingMethod();
        return null;
    }

    @Override
    public void manyArgumentsComplexOne(ComplexModel aModel4, String aString4) {
        method = new Object() {}.getClass().getEnclosingMethod();
    }

    @Override
    public List<ComplexModel> parameterizedOne(List<String> strings5) {
        method = new Object() {}.getClass().getEnclosingMethod();
        return null;
    }

    @Override
    public void variableArguments(int anInt6) {
        method = new Object() {}.getClass().getEnclosingMethod();
    }

    @Override
    public void variableArguments(int anInt7, ComplexModel aModel7) {
        method = new Object() {}.getClass().getEnclosingMethod();
    }

    @Override
    public void variableArguments(String aString8) {
        method = new Object() {}.getClass().getEnclosingMethod();
    }

}
