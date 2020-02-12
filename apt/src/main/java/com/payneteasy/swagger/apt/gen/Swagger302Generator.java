package com.payneteasy.swagger.apt.gen;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.payneteasy.swagger.apt.MethodMeta;
import com.payneteasy.swagger.apt.ParameterMeta;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import javax.annotation.concurrent.NotThreadSafe;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Swagger 3.0.2 (OpenAPI 3.0.2) json generator.<br/>
 * Swagger 3.0.2 json specification: https://swagger.io/specification/.<br/>
 * Just in case - swagger 2.0 json specification: https://swagger.io/specification/v2/.
 * <p/>
 * <b>Model classes with the same names and different packages are not supported!</b>
 * <p/>
 * Not thread-safe.<br/>
 * Class instance should be used only once.
 * <p/>
 * Sample json:
 * <pre>{@code
 * {
 *   "openapi" : "3.0.2",
 *   "info" : {
 *     "title" : "demo services",
 *     "description" : "demo services description",
 *     "version" : "1.0"
 *   },
 *   "servers" : [
 *     {
 *       "url" : "/demo/api",
 *       "description" : "Demo local server"
 *     }
 *   ],
 *   "components" : {
 *     "schemas" : {
 *       "ComplexModel" : {
 *         "type" : "object",
 *         "properties" : {
 *           "status" : {
 *             "type" : "integer"
 *           },
 *           "text" : {
 *             "type" : "string"
 *           }
 *         }
 *       }
 *     }
 *   },
 *   "paths" : {
 *     "/ISomeService/parameterizedOne" : {
 *       "post" : {
 *         "tags" : [
 *           "ISomeService"
 *         ],
 *         "summary" : "parameterizedOne method javadoc.",
 *         "requestBody" : {
 *           "required" : true,
 *           "content" : {
 *             "application/json" : {
 *               "schema" : {
 *                 "type" : "array",
 *                 "items" : {
 *                   "type" : "string"
 *                 }
 *               }
 *             }
 *           },
 *           "description" : "List&lt;String&gt; **strings5** *(some strings5.)*"
 *         },
 *         "responses" : {
 *           "200" : {
 *             "description" : "List&lt;ComplexModel&gt; *(return parameterizedOne.)*",
 *             "schema" : {
 *               "type" : "array",
 *               "items" : {
 *                 "$ref" : "#/definitions/ComplexModel"
 *               }
 *             }
 *           }
 *         }
 *       }
 *     }
 *   },
 *   "tags" : [
 *     {
 *       "name" : "ISomeService",
 *       "description" : "Some service."
 *     }
 *   ]
 * }
 * }</pre>
 *
 * @author dvponomarev, 05.02.2020
 */
@SuppressWarnings("FieldCanBeLocal")
@NotThreadSafe
public class Swagger302Generator {

    private static final String OPENAPI_VERSION = "3.0.2";

    /** Swagger services page: title. */
    private final String           title;
    /** Swagger services page: description. */
    private final String           description;
    /** Swagger services page: version. */
    private final String           version;
    /** Servers to try services. */
    private final List<ServerInfo> serverInfos = new ArrayList<>();

    private final ArgumentsParseStrategy argumentsParseStrategy;

    private final ObjectMapper    objectMapper        = new ObjectMapper();
    private final SchemaGenerator jsonSchemaGenerator = new SchemaGenerator(
            new SchemaGeneratorConfigBuilder(objectMapper, OptionPreset.PLAIN_JSON).
                    with(Option.DEFINITIONS_FOR_ALL_OBJECTS).
                    without(Option.VALUES_FROM_CONSTANT_FIELDS, Option.SCHEMA_VERSION_INDICATOR).
                    build()
    );

    // internals
    private ObjectNode root;
    private ObjectNode components;
    private ObjectNode schemas;
    private ObjectNode paths;


    public Swagger302Generator(
            String title, String description, String version, ArgumentsParseStrategy argumentsParseStrategy
    ) {
        this.title                  = title;
        this.description            = description;
        this.version                = version;
        this.argumentsParseStrategy = argumentsParseStrategy;
    }

    /**
     * @param path        relative path: {@code /demo/api}<br/>
     *                    or full url: {@code http://localhost/demo/api}
     * @param description server description.
     */
    public void addServer(String path, String description) {
        serverInfos.add(new ServerInfo(path, description));
    }

    /** Generate swagger json. */
    public String generateJsonString(Collection<ServiceInfo> services) {
        final ObjectNode aRoot = generateJson(services);

        try {
            return objectMapper.writeValueAsString(aRoot);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot create json", e);
        }
    }

    /** Generate swagger json tree to be able to customize it (add authorization for example). */
    public ObjectNode generateJson(Collection<ServiceInfo> services) {
        root = JsonNodeFactory.instance.objectNode();
        root.put("openapi", OPENAPI_VERSION);

        addInfo();
        addServers();
        addTags(services);

        //"components" : {
        //  "schemas" : {
        //    "ComplexModel" : {
        //      "type" : "object",
        //      "properties" : {
        //        "status" : {
        //          "type" : "integer"
        //        },
        //        "text" : {
        //          "type" : "string"
        //        }
        //      }
        //    }
        //  }
        //}
        components = root.putObject("components");
        schemas    = components.putObject("schemas");

        addPaths(services);

        return root;
    }

    /**
     * <pre>{@code
     * "info": {
     *     "title": "Swagger Petstore - OpenAPI 3.0",
     *     "description": "This is a sample Pet Store Server",
     *     "version": "1.0.4"
     * }
     * }</pre>
     */
    private void addInfo() {
        final ObjectNode info = root.putObject("info");
        info.put("title", title);
        info.put("description", description);
        info.put("version", version);
    }

    /**
     * <pre>{@code
     * "servers": [
     *     {
     *         "url": "/api/v3",
     *         "description": "dsc"
     *     }
     * ]
     * }</pre>
     */
    private void addServers() {
        final ArrayNode servers = root.putArray("servers");
        for (ServerInfo serverInfo : serverInfos) {
            final ObjectNode server = servers.addObject();
            server.put("url", serverInfo.path);
            server.put("description", serverInfo.description);
        }
    }

    /**
     * <pre>{@code
     * "tags": [
     *     {
     *         "name": "store",
     *         "description": "Operations about user"
     *     }
     * ]
     * }</pre>
     */
    private void addTags(Collection<ServiceInfo> services) {
        final ArrayNode tags = root.putArray("tags");
        for (ServiceInfo serviceInfo : services) {
            final ObjectNode tagNode = tags.addObject();
            tagNode.put("name", serviceInfo.name);
            tagNode.put("description", serviceInfo.javadoc);
        }
    }

    /**
     * <pre>{@code
     * "paths": {
     *     "/pet": {
     *         "post": {
     *             "tags": [
     *                 "pet"
     *             ],
     *             "summary": "Add a new pet to the store",
     *             "description": "Add a new pet to the store",
     *             "operationId": "addPet",
     *             "requestBody": {
     *                 "description": "Create a new pet in the store",
     *                 "content": {
     *                     "application/json": {
     *                         "schema": {
     *                             "$ref": "#/components/schemas/Pet"
     *                         }
     *                     }
     *                 },
     *                 "required": true
     *             },
     *             "responses": {
     *                 "200": {
     *                     "description": "Successful operation",
     *                     "content": {
     *                         "application/json": {
     *                             "schema": {
     *                                 "$ref": "#/components/schemas/Pet"
     *                             }
     *                         }
     *                     }
     *                 }
     *             }
     *         }
     *     }
     * }
     * }</pre>
     */
    private void addPaths(Collection<ServiceInfo> services) {
        paths = root.putObject("paths");
        for (ServiceInfo service : services) {
            for (ServiceMethodInfo serviceMethodInfo : service.methods.values()) {
                final MethodMeta methodMeta = serviceMethodInfo.methodMeta;
                final String     methodPath = ServiceUriUtil.toUri(service.name, methodMeta.methodId);
                final ObjectNode path       = paths.putObject(methodPath);
                final ObjectNode post       = path.putObject("post");
                post.putArray("tags").add(service.name);
                post.put("summary", methodMeta.javadoc);
                createParameters(post, serviceMethodInfo);
                createResponse(post, serviceMethodInfo);
            }
        }
    }

    private void createParameters(ObjectNode postNode, ServiceMethodInfo serviceMethodInfo) {
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

        final ObjectNode requestBody = postNode.putObject("requestBody");
        requestBody.put("required", true);
        final ObjectNode content         = requestBody.putObject("content");
        final ObjectNode applicationJson = content.putObject("application/json");

        switch (argumentsParseStrategy) {
            case MIXED: {
                if (parameterTypes.size() == 1) {
                    // single parameter

                    // For example:
                    //"requestBody" : {
                    //  "required" : true,
                    //  "content" : {
                    //    "application/json" : {
                    //      "schema" : {
                    //        "type" : "integer"
                    //      }
                    //    }
                    //  },
                    //  "description" : "int **anInt6** *(some anInt6.)*"
                    //}
                    applicationJson.set("schema", generateTypeSchema(method.getGenericParameterTypes()[0]));
                } else {
                    // multiple parameters

                    genMap(parameterTypes, parameterMetas, applicationJson);
                }
                break;
            }
            case MAP: {
                genMap(parameterTypes, parameterMetas, applicationJson);
                break;
            }
            default:
                throw new IllegalArgumentException(
                        String.format("Unsupported ArgumentsParseStrategy value '%s'.", argumentsParseStrategy)
                );
        }

        requestBody.put("description", getParametersDescription(parameterTypes, parameterMetas));
    }

    private void genMap(List<Type> parameterTypes, List<ParameterMeta> parameterMetas, ObjectNode applicationJson) {
        // For example:
        //"requestBody" :{
        //  "required" :true,
        //  "content" :{
        //    "application/json" :{
        //      "schema" :{
        //        "type" :"object",
        //        "properties" :{
        //          "anInt1" :{
        //            "type" :"integer"
        //          },
        //          "aString1" :{
        //            "type" :"string"
        //          }
        //        }
        //      }
        //    }
        //  },
        //  "description" :"int **anInt1** *(some anInt1.)*\nString **aString1** *(some aString1.)*"
        //}

        final ObjectNode schema = applicationJson.putObject("schema");
        schema.put("type", "object");
        final ObjectNode properties = schema.putObject("properties");

        final Iterator<ParameterMeta> parameterMetasIterator = parameterMetas.iterator();
        for (Type type : parameterTypes) {
            final String name = parameterMetasIterator.next().name;
            properties.set(name, generateTypeSchema(type));
        }
    }

    /**
     * Example of result:
     * <pre>{@code
     * long **id** *(ID)*  \n
     * long **typeId**  \n
     * com.example.model.LimitType **limitType**  \n
     * java.math.BigDecimal **limit**
     * }</pre>
     */
    @NotNull
    private String getParametersDescription(List<Type> parameterTypes, List<ParameterMeta> parameterMetas) {
        //markdown syntax
        final List<String>            parametersDescriptions = new ArrayList<>();
        final Iterator<ParameterMeta> parameterMetasIterator = parameterMetas.iterator();
        for (Type type : parameterTypes) {
            final ParameterMeta parameterMeta = parameterMetasIterator.next();
            final String        javadoc       = parameterMeta.javadoc.isEmpty() ? "" : " *(" + parameterMeta.javadoc + ")*";
            parametersDescriptions.add(
                    StringEscapeUtils.escapeHtml4(getTypeSimpleName(type)) + " **" + parameterMeta.name + "**" + javadoc
            );
        }
        return String.join("  \n", parametersDescriptions);
    }

    private void createResponse(ObjectNode post, ServiceMethodInfo serviceMethodInfo) {
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
            ObjectNode obj = generateTypeSchema(method.getGenericReturnType());
            ok.set("schema", obj);
        }
    }

    @NotNull
    private ObjectNode generateTypeSchema(Type type) {
        ObjectNode schema = (ObjectNode) jsonSchemaGenerator.generateSchema(type);

        //move definitions to 'schemas' area
        JsonNode definitions = schema.get("definitions");
        if (definitions != null) {
            Iterator<String> names = definitions.fieldNames();
            while (names.hasNext()) {
                String name = names.next();
                schemas.set(name, definitions.get(name));
            }
            schema.remove("definitions");
        }

        return schema;
    }

    static String getTypeSimpleName(Type type) {
        if (type instanceof Class) {
            return ((Class<?>) type).getSimpleName();
        } else if (type instanceof ParameterizedType) {
            final ParameterizedType parameterizedType = (ParameterizedType) type;
            final String parameters = Arrays.stream(parameterizedType.getActualTypeArguments()).
                    map(Swagger302Generator::getTypeSimpleName).
                    collect(Collectors.joining(","));
            return getTypeSimpleName(parameterizedType.getRawType()) + "<" + parameters + ">";
        } else {
            return type.getTypeName();
        }
    }

    private static class ServerInfo {
        /**
         * Relative path:<br/>
         * {@code /demo/api}
         * <p/>
         * or full url:<br/>
         * {@code http://localhost/demo/api}
         */
        final String path;
        final String description;

        ServerInfo(String path, String description) {
            this.path        = path;
            this.description = description;
        }
    }

}
