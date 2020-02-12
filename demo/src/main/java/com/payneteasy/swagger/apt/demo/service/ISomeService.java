package com.payneteasy.swagger.apt.demo.service;

import com.payneteasy.swagger.apt.annotation.ExportToSwagger;
import com.payneteasy.swagger.apt.demo.service.model.ComplexModel;
import com.payneteasy.swagger.apt.demo.service.model.EnumModel;

import java.util.List;

/**
 * Some service.
 *
 * @author dvponomarev, 27.09.2018
 */
public interface ISomeService {

    @ExportToSwagger
    void empty();

    /**
     * noArguments method javadoc.
     */
    @ExportToSwagger
    void noArguments();

    /**
     * noArguments2 method javadoc.
     *
     * @return return noArguments2.
     */
    @ExportToSwagger
    String noArguments2();

    /**
     * manyArguments method javadoc.
     *
     * @param anInt1   some anInt1.
     * @param aString1 some aString1.
     * @return return manyArguments.
     */
    @ExportToSwagger
    long manyArguments(int anInt1, String aString1);

    /**
     * manyArgumentsEnum method javadoc.
     *
     * @param aEnum2   some aEnum2.
     * @param anInt2   some anInt2.
     * @param aString2 some aString2.
     * @return return manyArgumentsEnum.
     */
    @ExportToSwagger
    String manyArgumentsEnum(EnumModel aEnum2, int anInt2, String aString2);

    /**
     * oneComplexArgument method javadoc.
     *
     * @param aModel3 some aModel3.
     * @return return oneComplexArgument.
     */
    @ExportToSwagger
    ComplexModel oneComplexArgument(ComplexModel aModel3);

    /**
     * manyArgumentsComplexOne method javadoc.
     *
     * @param aModel4  some aModel4.
     * @param aString4 some aString4.
     */
    @ExportToSwagger
    void manyArgumentsComplexOne(ComplexModel aModel4, String aString4);

    /**
     * parameterizedOne method javadoc.
     *
     * @param strings5 some strings5.
     * @return return parameterizedOne.
     */
    @ExportToSwagger
    List<ComplexModel> parameterizedOne(List<String> strings5);

    /**
     * variableArguments method javadoc.
     *
     * @param anInt6 some anInt6.
     */
    @ExportToSwagger
    void variableArguments(int anInt6);

    /**
     * variableArguments method javadoc.
     *
     * @param anInt7  some anInt7.
     * @param aModel7 some aModel7.
     */
    @ExportToSwagger("variableArguments_2")
    void variableArguments(int anInt7, ComplexModel aModel7);

    /**
     * variableArguments method javadoc.
     *
     * @param aString8 some aString8.
     */
    @ExportToSwagger("variableArguments_3")
    void variableArguments(String aString8);

}
