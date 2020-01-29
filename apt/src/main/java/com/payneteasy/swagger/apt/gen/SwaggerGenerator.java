package com.payneteasy.swagger.apt.gen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import com.payneteasy.swagger.apt.MethodMeta;
import com.payneteasy.swagger.apt.ParameterMeta;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * Json specification: https://swagger.io/specification/v2/.
 */
public class SwaggerGenerator {

    private final String                 title;
    private final String                 version;
    private final String                 basePath;
    private final ArgumentsParseStrategy argumentsParseStrategy;

    private final ObjectMapper        objectMapper = new ObjectMapper();
    private final JsonSchemaGenerator jsonSchemaGenerator;

    public SwaggerGenerator(String title, String version, String basePath, ArgumentsParseStrategy argumentsParseStrategy) {
        this.title                  = title;
        this.version                = version;
        this.basePath               = basePath;
        this.argumentsParseStrategy = argumentsParseStrategy;

        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        jsonSchemaGenerator = new JsonSchemaGenerator(objectMapper);
    }

    public String generateJson(Collection<ServiceInfo> services, String protocol, String host, int port) {
        ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
        createHeader(protocol, host, port, rootNode);
        ObjectNode definitions = rootNode.putObject("definitions");
        createPaths(rootNode.putObject("paths"), services, definitions);

        final ArrayNode tags = rootNode.putArray("tags");
        for (ServiceInfo serviceInfo : services) {
            final ObjectNode tagNode = tags.addObject();
            tagNode.put("name", serviceInfo.name);
            tagNode.put("description", serviceInfo.javadoc);
        }

        try {
            return objectMapper.writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot create json", e);
        }
    }

    private void createPaths(ObjectNode paths, Collection<ServiceInfo> services, ObjectNode definitions) {
        for (ServiceInfo service : services) {
            for (ServiceMethodInfo serviceMethodInfo : service.methods.values()) {
                addMethod(paths, service.name, serviceMethodInfo, definitions);
            }
        }
    }

    private void addMethod(ObjectNode paths, String serviceName, ServiceMethodInfo serviceMethodInfo, ObjectNode definitions) {
        final MethodMeta methodMeta = serviceMethodInfo.methodMeta;
        final String     methodPath = ServiceUriUtil.toUri(serviceName, methodMeta.methodId);
        final ObjectNode path       = paths.putObject(methodPath);
        final ObjectNode post       = path.putObject("post");
        post.putArray("tags").add(serviceName);
        post.put("summary", methodMeta.javadoc);
        createParameters(post, serviceMethodInfo, definitions);
        createResponse(post, serviceMethodInfo, definitions);
    }

    private void createParameters(ObjectNode postNode, ServiceMethodInfo serviceMethodInfo, ObjectNode definitions) {
        final Method     method         = serviceMethodInfo.method;
        final List<Type> parameterTypes = Arrays.asList(method.getGenericParameterTypes());
        if (parameterTypes.isEmpty()) {
            return;
        }

        final List<ParameterMeta> parameterMetas = serviceMethodInfo.methodMeta.parameters;
        if (parameterTypes.size() != parameterMetas.size()) {
            throw new IllegalStateException(
                    String.format(
                            "parameterTypes.size=%d, names.size=%d, they should be the same.",
                            parameterTypes.size(), parameterMetas.size()
                    )
            );
        }

        final ArrayNode  parameters = postNode.putArray("parameters");
        final ObjectNode body       = parameters.addObject();
        body.put("in", "body");
        body.put("name", "body");
        body.put("required", true);

        final Set<String> notSupportedTypes = new TreeSet<>();
        switch (argumentsParseStrategy) {
            case MIXED: {
                if (parameterTypes.size() == 1) {
                    // single parameter

                    // For example:
                    //"parameters" : [ {
                    //  "in" : "body",
                    //  "name" : "body",
                    //  "required" : true,
                    //  "description" : "java.util.Date **statusDate**",
                    //  "schema" : {
                    //      "title" : "Date",
                    //      "type" : "integer",
                    //      "format" : "utc-millisec"
                    //  }
                    //} ]
                    final ObjectNode obj = generateDefinitions(definitions, method.getGenericParameterTypes()[0], notSupportedTypes);
                    body.set("schema", obj);
                } else {
                    // multiple parameters

                    // For example:
                    //"parameters" : [ {
                    //  "in" : "body",
                    //  "name" : "body",
                    //  "required" : true,
                    //  "description" : "java.lang.Long **monitorId**\njava.lang.String **sendEmailNotif**",
                    //  "schema" : {
                    //      "type" : "object",
                    //      "properties" : {
                    //          "monitorId" : {
                    //              "title" : "Long",
                    //              "type" : "integer"
                    //          },
                    //          "sendEmailNotif" : {
                    //              "title" : "String",
                    //              "type" : "string"
                    //          }
                    //      }
                    //  }
                    //} ]
                    final ObjectNode schema = body.putObject("schema");
                    schema.put("type", "object");
                    final ObjectNode properties = schema.putObject("properties");

                    final Iterator<ParameterMeta> parameterMetasIterator = parameterMetas.iterator();
                    for (Type type : parameterTypes) {
                        final String name = parameterMetasIterator.next().name;

                        final ObjectNode obj = generateDefinitions(definitions, type, notSupportedTypes);
                        properties.set(name, obj);
                    }
                }
                break;
            }
            case MAP:
            case ARRAY:
            default:
                throw new IllegalArgumentException(
                        String.format("Unsupported ArgumentsParseStrategy value '%s'.", argumentsParseStrategy)
                );
        }

        body.put("description", getParametersDescription(parameterTypes, parameterMetas, notSupportedTypes));
    }

    @NotNull
    private ObjectNode generateDefinitions(ObjectNode definitions, Type type, Set<String> notSupportedTypes) {
        final JavaType javaType = objectMapper.constructType(type);
        final JsonNode jsonNode = jsonSchemaGenerator.generateJsonSchema(javaType);
        if (!jsonNode.has("type")) {
            //in some cases JsonSchemaGenerator prints warning "Not able to generate jsonSchema-info for type" and returns
            //incomplete model
            notSupportedTypes.add(javaType.toCanonical());
        }
        return extractDefinitions(definitions, jsonNode);
    }

    /**
     * Example of result:
     * <pre>{@code
     * long **id** *(ID)*
     * long **typeId**
     * com.example.model.LimitType **limitType**
     * java.math.BigDecimal **limit**
     * }</pre>
     */
    @NotNull
    private String getParametersDescription(List<Type> parameterTypes, List<ParameterMeta> parameterMetas,
                                            Collection<String> notSupportedTypes) {
        final List<String>            parametersDescriptions = new ArrayList<>();
        final Iterator<ParameterMeta> parameterMetasIterator = parameterMetas.iterator();
        for (Type type : parameterTypes) {
            final ParameterMeta parameterMeta = parameterMetasIterator.next();
            final String        javadoc       = parameterMeta.javadoc.isEmpty() ? "" : " *(" + parameterMeta.javadoc + ")*";
            parametersDescriptions.add(StringEscapeUtils.escapeHtml4(getTypeSimpleName(type)) + " **" + parameterMeta.name + "**" + javadoc);
        }
        final String notSupportedParamsAddition = notSupportedTypes.stream().
                map(typeName -> String.format("**[parameter type %s is not supported]**", typeName)).
                collect(Collectors.joining("\n"));
        return String.join("\n", parametersDescriptions) + (notSupportedParamsAddition.isEmpty() ? "" : "\n" + notSupportedParamsAddition);
    }

    private void createResponse(ObjectNode post, ServiceMethodInfo serviceMethodInfo, ObjectNode definitions) {
        final Method method        = serviceMethodInfo.method;
        final String returnJavadoc = serviceMethodInfo.methodMeta.returnJavadoc;

        ObjectNode responses = post.putObject("responses");
        ObjectNode ok        = responses.putObject("200");

        if (method.getReturnType().equals(void.class)) {
            ok.put("description", "empty");
        } else {
            final String returnDescription = StringEscapeUtils.escapeHtml4(getTypeSimpleName(method.getGenericReturnType())) +
                                             (StringUtils.isBlank(returnJavadoc) ? "" : " *(" + returnJavadoc + ")*");
            ok.put("description", returnDescription);
            JavaType   javaType = objectMapper.constructType(method.getGenericReturnType());
            JsonNode   schema   = jsonSchemaGenerator.generateJsonSchema(javaType);
            ObjectNode obj      = extractDefinitions(definitions, schema);
            ok.set("schema", obj);
        }
    }

    static String getTypeSimpleName(Type type) {
        if (type instanceof Class) {
            return ((Class<?>) type).getSimpleName();
        } else if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            final String parameters = Arrays.stream(parameterizedType.getActualTypeArguments()).
                    map(SwaggerGenerator::getTypeSimpleName).
                    collect(Collectors.joining(","));
            return getTypeSimpleName(parameterizedType.getRawType()) + "<" + parameters + ">";
        } else {
            return type.getTypeName();
        }
    }

    @NotNull
    private ObjectNode extractDefinitions(ObjectNode aDefinitions, JsonNode schema) {
        ObjectNode obj = schema.deepCopy();
        obj.remove("$schema");
        JsonNode definitions = obj.get("definitions");
        if (definitions != null) {
            Iterator<String> names = definitions.fieldNames();
            while (names.hasNext()) {
                String name = names.next();
                aDefinitions.set(name, definitions.get(name));
            }
            obj.remove("definitions");
        }
        return obj;
    }

    private void createHeader(String protocol, String host, int port, ObjectNode rootNode) {
        rootNode.put("swagger", "2.0");

        ObjectNode info = rootNode.putObject("info");
        info.put("title", title);
        info.put("version", version);

        rootNode.put("host", host + ":" + port);

        ArrayNode schemes = rootNode.putArray("schemes");
        schemes.add(protocol);
//        schemes.add("http");
//        schemes.add("https");

        rootNode.put("basePath", basePath);

        rootNode.putArray("produces").add("application/json");

//        ObjectNode securityDefinitions = rootNode.putObject("securityDefinitions");
//        ObjectNode apiKeyHeader        = securityDefinitions.putObject("APIKeyHeader");
//        apiKeyHeader.put("type", "apiKey");
//        apiKeyHeader.put("in", "header");
//        apiKeyHeader.put("name", "JSESSIONID");
    }

}
