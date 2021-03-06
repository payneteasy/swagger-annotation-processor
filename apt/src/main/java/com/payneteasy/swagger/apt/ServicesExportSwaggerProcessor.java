package com.payneteasy.swagger.apt;

import com.payneteasy.swagger.apt.annotation.ExportToSwagger;
import com.payneteasy.swagger.apt.annotation.MethodParam;
import com.payneteasy.swagger.apt.javadoc.JavadocParser;
import com.payneteasy.swagger.apt.javadoc.MethodJavadocInfo;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeSpec;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * https://www.baeldung.com/java-annotation-processing-builder <br/>
 * https://medium.com/@joachim.beckers/debugging-an-annotation-processor-using-intellij-idea-in-2018-cde72758b78a, see Option 1.
 *
 * @author dvponomarev, 27.09.2018
 */
@SupportedAnnotationTypes("com.payneteasy.swagger.apt.annotation.ExportToSwagger")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class ServicesExportSwaggerProcessor extends AbstractProcessor {

    /** To print debug message in project build log. */
    private static final boolean DEBUG_LOG = false;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        final Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(ExportToSwagger.class);
        if (DEBUG_LOG) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, (elements != null) ? "" + elements.size() : "null");
        }

        if (elements == null || elements.isEmpty()) {
            return false;
        }

        //collect methods and interfaces marked with @ExportToSwagger
        final Set<Element> methods    = new LinkedHashSet<>();
        final Set<Element> interfaces = new LinkedHashSet<>();
        for (Element element : elements) {
            if (element.getKind() == ElementKind.METHOD) {
                if (element.getEnclosingElement().getKind() != ElementKind.INTERFACE) {
                    printWarning(
                            String.format("The method %s is ignored since it is located not in interface.", element),
                            element
                    );
                    continue;
                }
                methods.add(element);
            } else if (element.getKind() == ElementKind.INTERFACE) {
                if (!element.getModifiers().contains(Modifier.PUBLIC)) {
                    printWarning(
                            String.format("The interface %s is ignored since it is not public.", element),
                            element
                    );
                    continue;
                }
                if (!ElementUtil.containsAtLeastOneMethod(element.getEnclosedElements())) {
                    printWarning(
                            String.format("The interface %s is ignored since it does not contain any methods.", element),
                            element
                    );
                    continue;
                }
                interfaces.add(element);
            } else {
                printWarning(
                        String.format("The element %s is ignored.", element),
                        element
                );
            }
        }

        //collect methods interfaces
        final Set<Element> methodsInterfaces = new HashSet<>();
        for (Element method : methods) {
            methodsInterfaces.add(method.getEnclosingElement());
        }

        //collect interfaces methods
        final Set<Element> interfacesMethods = new HashSet<>();
        for (Element anInterface : interfaces) {
            interfacesMethods.addAll(ElementUtil.methods(anInterface.getEnclosedElements()));
        }

        //combine interfaces and methods
        interfaces.addAll(methodsInterfaces);
        methods.addAll(interfacesMethods);

        //collect javadocs of the services (interfaces)
        //map: service class info -> interface element
        final Map<ClassInfo, Element> serviceElementsMap = new HashMap<>();
        for (Element anInterface : interfaces) {
            final String packageName   = anInterface.getEnclosingElement().toString();
            final String interfaceName = anInterface.getSimpleName().toString();

            serviceElementsMap.putIfAbsent(new ClassInfo(packageName, interfaceName), anInterface);
        }
        //map: service class info -> service class javadoc (raw)
        final Map<ClassInfo, String> serviceJavadocsMap = new HashMap<>();
        for (Map.Entry<ClassInfo, Element> entry : serviceElementsMap.entrySet()) {
            final ClassInfo classInfo   = entry.getKey();
            final Element   anInterface = entry.getValue();

            final String javadoc = StringUtils.trimToEmpty(getDocComment(anInterface));
            serviceJavadocsMap.put(classInfo, javadoc);
        }

        //collect all methods infos
        final List<MethodInfo> methodInfos =
                methods.stream().map(method -> toMethodInfo((ExecutableElement) method)).collect(Collectors.toList());
        //map: service class info -> list of service's method infos
        final Map<ClassInfo, List<MethodInfo>> map =
                methodInfos.stream().collect(Collectors.groupingBy(methodInfo -> methodInfo.classInfo));

        //validate
        //within a service: methods with the same MethodId are prohibited
        final StringBuilder validationErrorSB = new StringBuilder();
        for (Map.Entry<ClassInfo, List<MethodInfo>> entry : map.entrySet()) {
            //class
            final ClassInfo        classInfo        = entry.getKey();
            final List<MethodInfo> classMethodInfos = entry.getValue();

            final Map<MethodId, Integer> duplicateMethodIdsMap = checkServiceMethodsUnique(classMethodInfos);
            if (!duplicateMethodIdsMap.isEmpty()) {
                validationErrorSB.append(
                        String.format(
                                "In class %s there are following duplicated MethodId:%n",
                                classInfo.packageName + "." + classInfo.className
                        )
                );
                for (Map.Entry<MethodId, Integer> duplicateEntry : duplicateMethodIdsMap.entrySet()) {
                    validationErrorSB.append(
                            String.format(
                                    "%s (%d times)%n", duplicateEntry.getKey().id, duplicateEntry.getValue()
                            )
                    );
                }
            }
        }
        if (validationErrorSB.length() > 0) {
            throw new IllegalStateException("\n" + validationErrorSB.toString());
        }

        for (Map.Entry<ClassInfo, List<MethodInfo>> entry : map.entrySet()) {
            //class
            final ClassInfo        classInfo        = entry.getKey();
            final List<MethodInfo> classMethodInfos = entry.getValue();

            final List<MethodSpec> methodSpecs = new ArrayList<>();
            for (MethodInfo methodInfo : classMethodInfos) {
                //method
                //we generate static method with the same parameters to provide that it is unique as in origin service
                //method returns MethodMeta
                final List<ParameterSpec> parameterSpecs = methodInfo.methodParameters.stream().map(
                        parameterInfo -> ParameterSpec.builder(ClassName.get(parameterInfo.type), parameterInfo.name).build()
                ).collect(Collectors.toList());

                final MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(methodInfo.methodName)
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addParameters(parameterSpecs)
                        .returns(MethodMeta.class)
                        .addStatement("final $T<$T> _parameters = new $T<>()", List.class, ParameterMeta.class, ArrayList.class);
                for (ParameterInfo parameter : methodInfo.methodParameters) {
                    //parameter
                    methodBuilder.addStatement("_parameters.add(new $T($S, $S))", ParameterMeta.class, parameter.name, parameter.javadoc);
                }

                final MethodSpec methodSpec = methodBuilder
                        .addStatement(
                                "return new $T(\n" +
                                "new $T($S),\n" +
                                "$S,\n" +
                                "_parameters,\n" +
                                "$S\n" +
                                ")",
                                MethodMeta.class, MethodId.class, methodInfo.methodId.id, methodInfo.methodJavadoc, methodInfo.returnJavadoc
                        )
                        .build();
                methodSpecs.add(methodSpec);
            }

            final String classFullJavadoc = serviceJavadocsMap.get(classInfo);
            if (classFullJavadoc == null) {
                throw new IllegalStateException("Not found class javadoc for " + classInfo);
            }
            final String classJavadoc = JavadocParser.parseClass(classFullJavadoc);
            final MethodSpec methodSpec = MethodSpec.methodBuilder(MetaConstants.CLASS_JAVADOC_METHOD)
                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                    .returns(String.class)
                    .addStatement("return $S", classJavadoc).build();
            methodSpecs.add(methodSpec);

            final String name = classInfo.className + MetaConstants.META_SUFFIX;
            final TypeSpec classSpec = TypeSpec.classBuilder(name).addMethods(methodSpecs)
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL).build();

            try {
                final JavaFileObject file = processingEnv.getFiler().createSourceFile(classInfo.packageName + "." + name);
                try (Writer out = new OutputStreamWriter(file.openOutputStream(), StandardCharsets.UTF_8)) {
                    final JavaFile javaFile = JavaFile.builder(classInfo.packageName, classSpec).indent("    ").build();
                    javaFile.writeTo(out);
                }
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        return false;
    }

    private void printWarning(String message, Element element) {
        processingEnv.getMessager().printMessage(
                Diagnostic.Kind.MANDATORY_WARNING,
                ServicesExportSwaggerProcessor.class.getSimpleName() + ": " + message,
                element
        );
    }

    private MethodInfo toMethodInfo(ExecutableElement method) {
        final Element aClass   = method.getEnclosingElement();
        final Element aPackage = aClass.getEnclosingElement();

        final String   methodName        = method.getSimpleName().toString();
        final MethodId methodId          = ExportToSwaggerUtil.getMethodId(method.getAnnotation(ExportToSwagger.class), methodName);
        final String   className         = aClass.getSimpleName().toString();
        final String   packageName       = aPackage.toString();
        final String   fullMethodJavadoc = StringUtils.trimToEmpty(getDocComment(method));

        final MethodJavadocInfo methodJavadocInfo;
        try {
            methodJavadocInfo = JavadocParser.parseMethod(fullMethodJavadoc);
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Cannot parse javadoc of method %s.%s.%s", packageName, className, method), e);
        }

        //We are able to get method parameter name as it declared in source code with
        // javax.lang.model.element.VariableElement.getSimpleName().toString()
        //
        //see also https://community.oracle.com/blogs/emcmanus/2006/06/13/using-annotation-processors-save-method-parameter-names
        final List<ParameterInfo> parameterInfos           = new ArrayList<>();
        final Set<String>         parameterNames           = new HashSet<>();
        final Set<String>         duplicatedParameterNames = new HashSet<>();
        for (VariableElement param : method.getParameters()) {
            final MethodParam annotation = param.getAnnotation(MethodParam.class);

            final String paramName;
            if (annotation != null) {
                paramName = annotation.value();
            } else {
                paramName = param.getSimpleName().toString();
            }
            parameterInfos.add(
                    new ParameterInfo(
                            paramName, param.asType(),
                            Objects.toString(methodJavadocInfo.parametersJavadoc.get(paramName), "")
                    )
            );
            final boolean doesNotContain = parameterNames.add(paramName);
            if (!doesNotContain) {
                duplicatedParameterNames.add(paramName);
            }
        }
        if (!duplicatedParameterNames.isEmpty()) {
            final String message = String.format(
                    "The method %s defines following duplicated parameter names %s.",
                    method, String.join(",", duplicatedParameterNames)
            );
            printWarning(message, method);
            throw new IllegalStateException(message);
        }

        final MethodInfo methodInfo = new MethodInfo(
                new ClassInfo(packageName, className), methodId, methodName, methodJavadocInfo.methodJavadoc,
                parameterInfos, methodJavadocInfo.returnJavadoc
        );
        if (DEBUG_LOG) {
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "className = " + className);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "packageName = " + packageName);
            processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "methodInfo = " + methodInfo);
        }

        return methodInfo;
    }

    private String getDocComment(Element element) {
        return processingEnv.getElementUtils().getDocComment(element);
    }

    private Map<MethodId, Integer> checkServiceMethodsUnique(List<MethodInfo> classMethodInfos) {
        final Set<MethodId>          uniqueMethods = new HashSet<>();
        final Map<MethodId, Integer> map           = new HashMap<>();
        for (MethodInfo methodInfo : classMethodInfos) {
            if (uniqueMethods.contains(methodInfo.methodId)) {
                final Integer current = map.getOrDefault(methodInfo.methodId, 1);
                map.put(methodInfo.methodId, current + 1);
            } else {
                uniqueMethods.add(methodInfo.methodId);
            }
        }
        return map;
    }

}
