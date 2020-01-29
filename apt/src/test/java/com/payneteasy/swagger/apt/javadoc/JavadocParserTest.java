package com.payneteasy.swagger.apt.javadoc;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * @author dvponomarev, 31.01.2019
 */
public class JavadocParserTest {

    @Test
    public void parseClass() {
        assertEquals("abc", JavadocParser.parseClass("abc"));
        assertEquals("abc", JavadocParser.parseClass(" abc\n\n"));
        assertEquals("abc\ndef", JavadocParser.parseClass(" abc\ndef\n"));
        assertEquals(
                "A javadoc. \n" +
                "Def abc.",
                JavadocParser.parseClass(
                        "A javadoc. <br/>\n" +
                        "Def abc.\n\n" +
                        "@author dvponomarev, 31.01.2019"
                )
        );
        assertEquals(
                "A javadoc.\n" +
                "Determines swagger REST method path.\n" +
                "Got from {@link ExportToSwagger#value() ExportToSwagger.value()}, defaults to method name.",
                JavadocParser.parseClass(
                        "A javadoc.<br/>\n" +
                        "Determines swagger REST method path.<br/>\n" +
                        "Got from {@link ExportToSwagger#value() ExportToSwagger.value()}, defaults to method name.\n\n" +
                        "@author dvponomarev, 05.02.2019"
                )
        );
    }

    @Test
    public void parseMethod() {
        assertEquals(
                new MethodJavadocInfo("abc", Collections.emptyMap(), ""),
                JavadocParser.parseMethod(
                        "abc"
                )
        );
        assertEquals(
                new MethodJavadocInfo("abc", Collections.singletonMap("a", "b"), ""),
                JavadocParser.parseMethod(
                        "abc\n" +
                        "@param a b"
                )
        );
        assertEquals(
                new MethodJavadocInfo("", Collections.emptyMap(), ""),
                JavadocParser.parseMethod(
                        "@param"
                )
        );
        assertEquals(
                new MethodJavadocInfo("", Collections.emptyMap(), ""),
                JavadocParser.parseMethod(
                        "@param a"
                )
        );
        assertEquals(
                new MethodJavadocInfo("", Collections.emptyMap(), ""),
                JavadocParser.parseMethod(
                        "@abc"
                )
        );
        assertEquals(
                new MethodJavadocInfo("", Collections.singletonMap("a", "b"), ""),
                JavadocParser.parseMethod(
                        "@param a b"
                )
        );
        assertEquals(
                new MethodJavadocInfo("abc", Collections.singletonMap("a", "b"), "ret"),
                JavadocParser.parseMethod(
                        "abc\n" +
                        "@param a b\n" +
                        "@return ret"
                )
        );
        assertEquals(
                new MethodJavadocInfo("abc", Collections.singletonMap("a", "b"), "ret"),
                JavadocParser.parseMethod(
                        "abc\n" +
                        "@param a b\n" +
                        "@return ret"
                )
        );
        assertEquals(
                new MethodJavadocInfo(
                        "RRRR abc.",
                        ImmutableMap.of(
                                "a", "b",
                                "c", "d e f"
                        ),
                        "ret abc"
                ),
                JavadocParser.parseMethod(
                        " RRRR abc.  \n" +
                        "\n\n" +
                        "@param a b \n" +
                        "@param c d e f \n" +
                        "@return ret abc\n" +
                        "@author dvponomarev, 31.01.2019"
                )
        );
    }

    @Test
    public void parseMethodSpecialCases() {
        assertEquals(
                new MethodJavadocInfo(
                        "Changes {@link a} status.",
                        ImmutableMap.of(
                                "statusId", "ID of the status",
                                "aEnabled", "if true, status will be enabled, otherwise disabled"
                        ),
                        ""
                ),
                JavadocParser.parseMethod(
                        "Changes {@link a} status.\n" +
                        "\n" +
                        "@param statusId\n" +
                        "            ID of the status\n" +
                        "@param aEnabled\n" +
                        "            if true, status will be enabled, otherwise disabled"
                )
        );
        assertEquals(
                new MethodJavadocInfo(
                        "Abc {@code a} def\n" +
                        "ghi jkl.",
                        ImmutableMap.of(
                                "progressId", "ID of the progress",
                                "params", "some params"
                        ),
                        ""
                ),
                JavadocParser.parseMethod(
                        "Abc {@code a} def\n" +
                        "ghi jkl.\n" +
                        "\n" +
                        "@param progressId ID of the progress\n" +
                        "@param params     some params\n" +
                        "@throws Exception\n" +
                        "@see Exception"
                )
        );
        assertEquals(
                new MethodJavadocInfo(
                        "Abc {@code a} def\n" +
                        "ghi jkl.",
                        ImmutableMap.of(
                                "progressId", "{@code a} ID of the progress",
                                "params", "some params"
                        ),
                        "ret\nret"
                ),
                JavadocParser.parseMethod(
                        "Abc {@code a} def\n" +
                        "ghi jkl.\n" +
                        "\n" +
                        "@param abc       \n" +
                        "@param progressId      {@code a} ID of the progress      \n" +
                        "@param params        some params\n" +
                        "@throws      Exception\n" +
                        "@return   ret\n" +
                        "ret    \n" +
                        "\n" +
                        "@see Exception"
                )
        );
        assertEquals(
                new MethodJavadocInfo(
                        "Abc {@code a} def ghi jkl.",
                        ImmutableMap.of(
                                "progressId", "{@code a} ID of the progress",
                                "params", "some params"
                        ),
                        "ret ret"
                ),
                JavadocParser.parseMethod(
                        "Abc {@code a} def ghi jkl. " +
                        "@param abc " +
                        "@param progressId {@code a} ID of the progress " +
                        "@param params some params " +
                        "@throws Exception " +
                        "@return ret ret " +
                        "@see Exception"
                )
        );
    }

}
