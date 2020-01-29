package com.payneteasy.swagger.apt;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author dvponomarev, 18.01.2019
 */
class MethodInfo {

    @NotNull
    final ClassInfo           classInfo;
    @NotNull
    final MethodId            methodId;
    @NotNull
    final String              methodName;
    /** Method javadoc, only head is included, no {@code @param}, {@code @return} included. */
    @NotNull
    final String              methodJavadoc;
    @NotNull
    final List<ParameterInfo> methodParameters;
    /** Method @return javadoc. */
    @NotNull
    final String              returnJavadoc;

    MethodInfo(@NotNull ClassInfo classInfo, @NotNull MethodId methodId, @NotNull String methodName, @NotNull String methodJavadoc,
               @NotNull List<ParameterInfo> methodParameters, @NotNull String returnJavadoc) {
        this.classInfo        = classInfo;
        this.methodId         = methodId;
        this.methodName       = methodName;
        this.methodJavadoc    = methodJavadoc;
        this.methodParameters = methodParameters;
        this.returnJavadoc    = returnJavadoc;
    }

    @Override
    public String toString() {
        return "MethodInfo{" +
               "classInfo=" + classInfo +
               ", methodId='" + methodId + '\'' +
               ", methodName='" + methodName + '\'' +
               ", methodJavadoc='" + methodJavadoc + '\'' +
               ", methodParameters=" + methodParameters +
               ", returnJavadoc='" + returnJavadoc + '\'' +
               '}';
    }

}
