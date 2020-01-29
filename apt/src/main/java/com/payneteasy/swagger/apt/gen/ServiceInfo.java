package com.payneteasy.swagger.apt.gen;

import com.payneteasy.swagger.apt.ExportToSwagger;
import com.payneteasy.swagger.apt.ExportToSwaggerUtil;
import com.payneteasy.swagger.apt.MetaClassUtil;
import com.payneteasy.swagger.apt.MethodId;
import com.payneteasy.swagger.apt.MethodMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;

public class ServiceInfo {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceInfo.class);

    private final Object                           bean;
    public final  Class<?>                         iface;
    public final  String                           name;
    public final  Map<MethodId, ServiceMethodInfo> methods = new TreeMap<>();
    public final  String                           javadoc;

    public ServiceInfo(Class<?> aInterface, Object aBean, String aName) {
        bean  = aBean;
        iface = aInterface;
        name  = aName;

        for (Method method : iface.getMethods()) {
            if (method.isAnnotationPresent(ExportToSwagger.class)) {
                final MethodId methodId = ExportToSwaggerUtil.getMethodId(method.getAnnotation(ExportToSwagger.class), method.getName());

                if (methods.containsKey(methodId)) {
                    throw new IllegalStateException(String.format("MethodId %s already exist in service %s", methodId, name));
                }
                final MethodMeta methodMeta = MetaClassUtil.getServiceMethodMetaInfo(iface, method);
                if (methodMeta == null) {
                    throw new IllegalStateException(String.format("Not found meta info for method %s.", method));
                }
                methods.put(methodId, new ServiceMethodInfo(method, methodMeta));
            }
        }

        javadoc = MetaClassUtil.getServiceJavadoc(iface);
        if (javadoc == null) {
            LOG.warn("Not found service '{}' javadoc.", name);
        }
    }

    @Nullable
    public Object invokeMethod(@NotNull MethodId methodId, @Nullable Object... aArguments) {
        final Method method = methods.get(methodId).method;
        try {
            return (aArguments == null) ? method.invoke(bean) : method.invoke(bean, aArguments);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot invoke " + method + " with arguments " + Arrays.toString(aArguments), e);
        }
    }

    @Override
    public String toString() {
        return "ServiceInfo{" +
               "iface=" + iface +
               '}';
    }

}
