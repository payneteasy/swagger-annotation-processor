package com.payneteasy.swagger.apt;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author dvponomarev, 05.02.2019
 */
public class ExportToSwaggerUtil {

    @NotNull
    public static MethodId getMethodId(@NotNull ExportToSwagger annotation, @NotNull String methodName) {
        final String methodIdValue = annotation.value();
        return new MethodId(StringUtils.isEmpty(methodIdValue) ? methodName : methodIdValue);
    }

}
