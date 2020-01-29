package com.payneteasy.swagger.apt.javadoc;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

/**
 * @author dvponomarev, 31.01.2019
 */
public class MethodJavadocInfo {

    /** Method javadoc, only head is included, no {@code @param}, {@code @return} included. */
    @NotNull
    public final String              methodJavadoc;
    /**
     * Method parameters javadocs got from full method javadoc.
     * <p/>
     * Map:
     * <ul>
     * <li>key - parameter name (as it declared in method javadoc), not {@code null}</li>
     * <li>value - parameter javadoc, not {@code null}</li>
     * </ul>
     */
    @NotNull
    public final Map<String, String> parametersJavadoc;
    /** Method @return javadoc. */
    @NotNull
    public final String              returnJavadoc;

    public MethodJavadocInfo(@NotNull String methodJavadoc, @NotNull Map<String, String> parametersJavadoc, @NotNull String returnJavadoc) {
        this.methodJavadoc     = methodJavadoc;
        this.parametersJavadoc = parametersJavadoc;
        this.returnJavadoc     = returnJavadoc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MethodJavadocInfo that = (MethodJavadocInfo) o;
        return methodJavadoc.equals(that.methodJavadoc) &&
               parametersJavadoc.equals(that.parametersJavadoc) &&
               returnJavadoc.equals(that.returnJavadoc);
    }

    @Override
    public String toString() {
        return "MethodJavadocInfo{" +
               "methodJavadoc='" + methodJavadoc + '\'' +
               ", parametersJavadoc=" + parametersJavadoc +
               ", returnJavadoc='" + returnJavadoc + '\'' +
               '}';
    }

}
