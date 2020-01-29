package com.payneteasy.swagger.apt;

import org.jetbrains.annotations.NotNull;

/**
 * Method parameter meta data.
 *
 * @author dvponomarev, 31.01.2019
 */
public class ParameterMeta {

    /** Method parameter name as it declared in source code. */
    @NotNull
    public final String name;
    /** Method parameter javadoc got from method javadoc. */
    @NotNull
    public final String javadoc;

    public ParameterMeta(@NotNull String name, @NotNull String javadoc) {
        this.name    = name;
        this.javadoc = javadoc;
    }

    @Override
    public String toString() {
        return "ParameterMeta{" +
               "name='" + name + '\'' +
               ", javadoc='" + javadoc + '\'' +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final ParameterMeta that = (ParameterMeta) o;
        return name.equals(that.name) &&
               javadoc.equals(that.javadoc);
    }

}
