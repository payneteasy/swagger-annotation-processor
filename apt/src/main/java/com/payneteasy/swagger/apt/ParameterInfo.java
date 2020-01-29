package com.payneteasy.swagger.apt;

import org.jetbrains.annotations.NotNull;

import javax.lang.model.type.TypeMirror;

/**
 * @author dvponomarev, 18.01.2019
 */
class ParameterInfo {

    /** Method parameter name as it declared in source code. */
    @NotNull
    final String     name;
    @NotNull
    final TypeMirror type;
    /** Method parameter javadoc got from full method javadoc. */
    @NotNull
    final String     javadoc;

    ParameterInfo(@NotNull String name, @NotNull TypeMirror type, @NotNull String javadoc) {
        this.name    = name;
        this.type    = type;
        this.javadoc = javadoc;
    }

    @Override
    public String toString() {
        return "ParameterInfo{" +
               "name='" + name + '\'' +
               ", type=" + type +
               ", javadoc='" + javadoc + '\'' +
               '}';
    }

}
