package com.payneteasy.swagger.apt;

import com.payneteasy.swagger.apt.annotation.ExportToSwagger;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * Utils to work with {@code *_Meta} classes generated at annotation processing build stage.
 *
 * @author dvponomarev, 24.01.2019
 * @see ExportToSwagger
 */
public class MetaClassUtil {

    /**
     * Obtain service javadoc.
     *
     * @param service service class, interface usually. It is that class/interface that has method(s) with
     *                {@link ExportToSwagger @ExportToSwagger} annotation.
     * @return service javadoc or {@code null} if not found.
     */
    @Nullable
    public static String getServiceJavadoc(Class service) {
        try {
            final Class<?> metaClass = getMetaClass(service);

            //invoke static method with default arguments
            return (String) metaClass.getMethod(MetaConstants.CLASS_JAVADOC_METHOD).invoke(null);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    /**
     * Obtain service method meta info, stored at annotation processing build stage.
     *
     * @param service service class, interface usually. It is that class/interface that has method(s) with
     *                {@link ExportToSwagger @ExportToSwagger} annotation.
     * @param method  service method, it is not required to be the service class method, it only should have the same name and parameters
     *                (return type does not matter, because it is not required to invoke the method).
     * @return service method meta info or {@code null} if not found.
     */
    @Nullable
    public static MethodMeta getServiceMethodMetaInfo(Class service, Method method) {
        try {
            final Class<?> metaClass = getMetaClass(service);

            //invoke static method with default arguments
            return (MethodMeta) metaClass.getMethod(method.getName(), method.getParameterTypes()).
                    invoke(null, getMethodDefaultArguments(method));
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    private static Class<?> getMetaClass(Class service) throws ClassNotFoundException {
        return Class.forName(service.getName() + MetaConstants.META_SUFFIX);
    }

    @SuppressWarnings({"unchecked"})
    private static Object[] getMethodDefaultArguments(Method method) {
        final List arguments = new ArrayList();
        for (Class<?> type : method.getParameterTypes()) {
            arguments.add(getDefaultArgument(type));
        }
        return arguments.toArray();
    }

    @SuppressWarnings("UnnecessaryBoxing")
    private static Object getDefaultArgument(Class type) {
        if (type.isPrimitive()) {
            final String name = type.getName();
            switch (name) {
                case "byte":
                    return Byte.valueOf((byte) 0);
                case "short":
                    return Short.valueOf((short) 0);
                case "int":
                    return Integer.valueOf(0);
                case "long":
                    return Long.valueOf(0L);
                case "float":
                    return Float.valueOf(0);
                case "double":
                    return Double.valueOf(0);
                case "boolean":
                    return Boolean.FALSE;
                case "char":
                    return Character.valueOf('0');
                default:
                    throw new IllegalArgumentException(String.format("Type '%s' is not supported.", name));
            }
        } else {
            return null;
        }
    }

}
