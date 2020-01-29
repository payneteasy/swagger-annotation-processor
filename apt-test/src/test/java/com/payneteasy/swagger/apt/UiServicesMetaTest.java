package com.payneteasy.swagger.apt;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * @author dvponomarev, 24.01.2019
 */
public class UiServicesMetaTest {

    private SomeServiceMetaTestImpl serviceMetaTestImpl;

    @Before
    public void before() {
        this.serviceMetaTestImpl = new SomeServiceMetaTestImpl();
    }

    @SuppressWarnings("Convert2MethodRef")
    @Test
    public void test() throws Exception {
        MethodMeta methodMeta = (MethodMeta) ISomeService_Meta.class.getMethod("empty").invoke(null);
        assertEquals("", methodMeta.javadoc);
        assertTrue(methodMeta.parameters.isEmpty());

        testOne(() -> serviceMetaTestImpl.noArguments());
        testOne(() -> serviceMetaTestImpl.noArguments2());
        testOne(() -> serviceMetaTestImpl.manyArguments(0, null),
                getParameterMeta("anInt1"), getParameterMeta("aString1"));
        testOne(() -> serviceMetaTestImpl.manyArgumentsEnum(null, 0, null),
                getParameterMeta("aEnum2"), getParameterMeta("anInt2"), getParameterMeta("aString2"));
        testOne(() -> serviceMetaTestImpl.oneComplexArgument(null),
                getParameterMeta("aModel3"));
        testOne(() -> serviceMetaTestImpl.manyArgumentsComplexOne(null, null),
                getParameterMeta("aModel4"), getParameterMeta("aString4"));
        testOne(() -> serviceMetaTestImpl.parameterizedOne(null),
                getParameterMeta("strings5"));
        testOne(() -> serviceMetaTestImpl.variableArguments(0),
                getParameterMeta("anInt6"));
        testOne(() -> serviceMetaTestImpl.variableArguments(0, null),
                getParameterMeta("anInt7"), getParameterMeta("aModel7"));
        testOne(() -> serviceMetaTestImpl.variableArguments(null),
                getParameterMeta("aString8"));

        TestCase.assertEquals("Some service.", MetaClassUtil.getServiceJavadoc(serviceMetaTestImpl.getClass().getInterfaces()[0]));
    }

    private ParameterMeta getParameterMeta(String name) {
        return new ParameterMeta(name, "some " + name + ".");
    }

    private void testOne(Runnable methodInvokeCode, ParameterMeta... expectedParameters) {
        methodInvokeCode.run();
        final Method method = serviceMetaTestImpl.method;

        final MethodMeta methodMeta = MetaClassUtil.getServiceMethodMetaInfo(method.getDeclaringClass().getInterfaces()[0], method);

        assertNotNull(methodMeta);
        assertTrue(methodMeta.javadoc.startsWith(String.format("%s method javadoc.", method.getName())));
        assertParameters(methodMeta.parameters, expectedParameters);
    }

    private void assertParameters(List<ParameterMeta> actual, ParameterMeta... expected) {
        assertEquals(Arrays.asList(expected), actual);
    }

}
