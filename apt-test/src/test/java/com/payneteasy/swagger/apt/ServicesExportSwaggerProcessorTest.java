package com.payneteasy.swagger.apt;

import com.google.testing.compile.Compilation;
import com.google.testing.compile.CompilationSubject;
import com.google.testing.compile.Compiler;
import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * https://medium.com/inloopx/weapons-for-boilerplate-destruction-pt-3-testing-the-annotation-processor-f486fb19f5b1
 *
 * @author dvponomarev, 27.09.2018
 */
public class ServicesExportSwaggerProcessorTest {

    @Test
    public void test() {
        Compilation compilation = Compiler.javac()
                .withProcessors(new ServicesExportSwaggerProcessor())
                .compile(JavaFileObjects.forResource("com/payneteasy/swagger/apt/ISomeService2.java"));
        assertTrue(compilation.generatedSourceFile("com/payneteasy/swagger/apt/ISomeService2_Meta").isPresent());
        CompilationSubject.assertThat(compilation).succeeded();
    }

    @Test
    public void testDuplicateMethods() {
        {
            Compilation compilation = Compiler.javac()
                    .withProcessors(new ServicesExportSwaggerProcessor())
                    .compile(JavaFileObjects.forSourceString(
                            "com.payneteasy.swagger.apt.ISomeDynamicService3",
                            "package com.payneteasy.swagger.apt;\n" +
                            "import com.payneteasy.swagger.apt.annotation.ExportToSwagger;\n" +
                            "interface ISomeService3 {\n" +
                            "    @ExportToSwagger\n" +
                            "    void methodA(int i);\n" +
                            "}"
                    ));
            CompilationSubject.assertThat(compilation).succeeded();
        }
        {
            try {
                //noinspection ResultOfMethodCallIgnored
                Compiler.javac()
                        .withProcessors(new ServicesExportSwaggerProcessor())
                        .compile(JavaFileObjects.forSourceString(
                                "com.payneteasy.swagger.apt.ISomeDynamicService3",
                                "package com.payneteasy.swagger.apt;\n" +
                                "import com.payneteasy.swagger.apt.annotation.ExportToSwagger;\n" +
                                "interface ISomeService3 {\n" +
                                "    @ExportToSwagger\n" +
                                "    void methodA(int i);\n" +
                                "    @ExportToSwagger\n" +
                                "    void methodA(String s);\n" +
                                "}"
                        ));
                fail();
            } catch (RuntimeException e) {
                assertEquals(e.getCause().getClass(), IllegalStateException.class);
            }
        }
        {
            Compilation compilation = Compiler.javac()
                    .withProcessors(new ServicesExportSwaggerProcessor())
                    .compile(JavaFileObjects.forSourceString(
                            "com.payneteasy.swagger.apt.ISomeDynamicService3",
                            "package com.payneteasy.swagger.apt;\n" +
                            "import com.payneteasy.swagger.apt.annotation.ExportToSwagger;\n" +
                            "interface ISomeService3 {\n" +
                            "    @ExportToSwagger\n" +
                            "    void methodA(int i);\n" +
                            "    @ExportToSwagger(\"methodA_2\")\n" +
                            "    void methodA(String s);\n" +
                            "}"
                    ));
            CompilationSubject.assertThat(compilation).succeeded();
        }
    }

}