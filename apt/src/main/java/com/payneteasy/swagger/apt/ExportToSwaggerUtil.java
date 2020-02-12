package com.payneteasy.swagger.apt;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author dvponomarev, 05.02.2019
 */
public class ExportToSwaggerUtil {

    @NotNull
    public static MethodId getMethodId(@Nullable ExportToSwagger annotation, @NotNull String methodName) {
        if (annotation == null) {
            return new MethodId(methodName);
        }
        final String methodIdValue = annotation.value();
        return new MethodId(StringUtils.isEmpty(methodIdValue) ? methodName : methodIdValue);
    }

}
