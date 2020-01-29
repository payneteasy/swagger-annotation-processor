package com.payneteasy.swagger.apt.gen;

import com.payneteasy.swagger.apt.MethodId;

/**
 * Conversion between uri and (service name, MethodId) back and forth.
 *
 * @author dvponomarev, 05.02.2019
 */
public class ServiceUriUtil {

    public static ServiceMethodId fromUri(String uri) {
        final String[] paths         = uri.split("/");
        final String   serviceName   = paths[paths.length - 2];
        final String   methodIdValue = paths[paths.length - 1];
        return new ServiceMethodId(serviceName, new MethodId(methodIdValue));
    }

    public static String toUri(String serviceName, MethodId methodId) {
        return "/" + serviceName + "/" + methodId.id;
    }

}
