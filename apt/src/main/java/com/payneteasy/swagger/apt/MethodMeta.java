package com.payneteasy.swagger.apt;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Method meta data.
 *
 * @author dvponomarev, 18.01.2019
 */
public class MethodMeta {

    @NotNull
    public final MethodId            methodId;
    /** Method javadoc, only head is included, no {@code @param}, {@code @return} included. */
    @NotNull
    public final String              javadoc;
    /** Method parameters meta data. */
    @NotNull
    public final List<ParameterMeta> parameters;
    /** Method @return javadoc. */
    @NotNull
    public final String              returnJavadoc;

    public MethodMeta(@NotNull MethodId methodId, @NotNull String javadoc, @NotNull List<ParameterMeta> parameters,
                      @NotNull String returnJavadoc) {
        this.methodId      = methodId;
        this.javadoc       = javadoc;
        this.parameters    = parameters;
        this.returnJavadoc = returnJavadoc;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final MethodMeta that = (MethodMeta) o;
        return methodId.equals(that.methodId) &&
               javadoc.equals(that.javadoc) &&
               parameters.equals(that.parameters) &&
               returnJavadoc.equals(that.returnJavadoc);
    }

}
