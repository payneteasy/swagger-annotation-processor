package com.payneteasy.swagger.apt.demo.controller;

import com.payneteasy.swagger.apt.annotation.ExportToSwagger;
import com.payneteasy.swagger.apt.gen.ServiceInfo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class ServiceInfoSearcher {

    private static final Logger LOG = LoggerFactory.getLogger(ServiceInfoSearcher.class);

    public static Map<String, ServiceInfo> searchServices(BeanFactory aContext, String aBasePackage)
            throws ClassNotFoundException {
        ClassPathScanningCandidateComponentProvider scanningProvider = new ClassPathScanningCandidateComponentProvider(false);
        scanningProvider.addIncludeFilter(new AnnotationTypeFilter(Service.class));
        Set<BeanDefinition>      components = scanningProvider.findCandidateComponents(aBasePackage);
        Map<String, ServiceInfo> services   = new TreeMap<>();

        for (BeanDefinition component : components) {
            String   className = component.getBeanClassName();
            Class<?> clazz     = Thread.currentThread().getContextClassLoader().loadClass(className);
            for (Class<?> iface : clazz.getInterfaces()) {
                if (isServiceForExport(iface)) {
                    final Service serviceAnnotation     = clazz.getAnnotation(Service.class);
                    final String  annotationServiceName = serviceAnnotation.value();

                    final String serviceName = StringUtils.isNotBlank(annotationServiceName) ? annotationServiceName : iface.getSimpleName();

                    final ServiceInfo previous;
                    if (StringUtils.isNotBlank(annotationServiceName)) {
                        previous = services.put(
                                serviceName,
                                new ServiceInfo(iface, aContext.getBean(annotationServiceName), serviceName)
                        );
                    } else {
                        previous = services.put(serviceName, new ServiceInfo(iface, aContext.getBean(iface), serviceName));
                    }
                    if ((previous != null) && (previous.iface != iface)) {
                        throw new IllegalStateException(
                                String.format(
                                        "Service with name '%s' was already found. Previous: %s, new: %s.",
                                        serviceName, previous.iface, iface
                                )
                        );
                    }
                }
            }
        }

        return services;
    }

    private static boolean isServiceForExport(Class<?> iface) {
        if (iface.isAnnotationPresent(ExportToSwagger.class)) {
            return true;
        }
        for (Method method : iface.getMethods()) {
            if (method.isAnnotationPresent(ExportToSwagger.class)) {
                return true;
            }
        }
        LOG.debug(
                "Service {} is not marked with @{} annotation and it has no methods marked",
                iface.getSimpleName(), ExportToSwagger.class.getSimpleName()
        );
        return false;
    }

}
