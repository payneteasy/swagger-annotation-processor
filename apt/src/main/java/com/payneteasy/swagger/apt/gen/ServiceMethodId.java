package com.payneteasy.swagger.apt.gen;

import com.payneteasy.swagger.apt.MethodId;
import org.jetbrains.annotations.NotNull;

/**
 * @author dvponomarev, 04.02.2019
 */
public class ServiceMethodId {

    @NotNull
    public final String   serviceName;
    @NotNull
    public final MethodId methodId;

    public ServiceMethodId(@NotNull String serviceName, @NotNull MethodId methodId) {
        this.serviceName = serviceName;
        this.methodId    = methodId;
    }

    @Override
    public String toString() {
        return "ServiceMethodId{" +
               "serviceName='" + serviceName + '\'' +
               ", methodId=" + methodId +
               '}';
    }

}
