package com.payneteasy.swagger.apt.gen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.payneteasy.swagger.apt.ParameterMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dvponomarev, 29.01.2020
 */
public class ServiceInvoker {

    /**
     * Map:
     * <ul>
     * <li>key - service name</li>
     * <li>value - service info</li>
     * </ul>
     */
    private final Map<String, ServiceInfo> services;
    private final Gson                     gson;
    private final ArgumentsParseStrategy   argumentsParseStrategy;

    public ServiceInvoker(@NotNull Map<String, ServiceInfo> services, @NotNull ArgumentsParseStrategy argumentsParseStrategy) {
        this.services               = services;
        this.argumentsParseStrategy = argumentsParseStrategy;

        final GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();

        //to serialize/deserialize Date as long
        gsonBuilder.registerTypeAdapter(
                Date.class,
                (JsonDeserializer<Date>) (json, typeOfT, aContext) -> new Date(json.getAsJsonPrimitive().getAsLong())
        ).registerTypeAdapter(
                Date.class,
                (JsonSerializer<Date>) (date, type, aContext) -> new JsonPrimitive(date.getTime())
        );

        //to serialize/deserialize Enum throwing exception on wrong enum value deserialization,
        //by default GSON deserializes wrong enum value to null !
        //see https://github.com/google/gson/issues/608
        //noinspection unchecked,rawtypes
        gsonBuilder.registerTypeHierarchyAdapter(
                Enum.class,
                (JsonSerializer<Enum<?>>) (aEnum, type, aContext) -> new JsonPrimitive(aEnum.name())
        ).registerTypeHierarchyAdapter(
                Enum.class,
                (JsonDeserializer<Enum<?>>) (jsonElement, type, aContext) ->
                        Enum.valueOf((Class<Enum>) type, jsonElement.getAsString())
        );

        this.gson = gsonBuilder.create();
    }

    @Nullable
    public String invokeServiceMethod(@NotNull ServiceMethodId serviceMethodId, @NotNull String argumentsJson) {
        final ServiceInfo serviceInfo = services.get(serviceMethodId.serviceName);
        if (serviceInfo == null) {
            throw new IllegalStateException("No service " + serviceMethodId.serviceName + " found");
        }
        final ServiceMethodInfo serviceMethodInfo = serviceInfo.methods.get(serviceMethodId.methodId);
        if (serviceMethodInfo == null) {
            throw new IllegalStateException(String.format("No service method %s found.", serviceMethodId));
        }

        final Object[] arguments = getArguments(serviceMethodInfo.method, argumentsJson, serviceMethodInfo.methodMeta.parameters);
        final Object   result    = serviceInfo.invokeMethod(serviceMethodId.methodId, arguments);

        return gson.toJson(result);
    }

    /**
     * @param json           method arguments serialized in json.
     * @param parameterMetas method parameters meta infos.
     * @return method arguments, {@code null} or empty array in case of no arguments.
     */
    @Nullable
    private Object[] getArguments(@NotNull Method method, @NotNull String json, @NotNull List<ParameterMeta> parameterMetas) {
        final List<Type> parameterTypes = Arrays.asList(method.getGenericParameterTypes());
        if (parameterTypes.size() != parameterMetas.size()) {
            throw new IllegalStateException(
                    String.format(
                            "parameterTypes.size=%d, parameterMetas.size=%d, they should be the same.",
                            parameterTypes.size(), parameterMetas.size()
                    )
            );
        }

        switch (argumentsParseStrategy) {
            case MIXED:
                if (parameterTypes.size() == 1) {
                    // single parameter
                    final Object argument = gson.fromJson(json, parameterTypes.get(0));
                    return new Object[]{argument};
                } else {
                    // 0 or multiple parameters
                    return parseArgumentsAsMap(json, parameterMetas, parameterTypes);
                }
            case MAP:
                return parseArgumentsAsMap(json, parameterMetas, parameterTypes);
            default:
                throw new IllegalArgumentException(
                        String.format("Unsupported ArgumentsParseStrategy value '%s'.", argumentsParseStrategy)
                );
        }
    }

    @Nullable
    private Object[] parseArgumentsAsMap(
            @NotNull String json, @NotNull List<ParameterMeta> parameterMetas, @NotNull List<Type> parameterTypes
    ) {
        final JsonElement rootElement = JsonParser.parseString(json);
        if (rootElement.isJsonNull()) {
            //no parameters
            return null;
        }

        final JsonObject root = rootElement.getAsJsonObject();

        final List<?>                 arguments              = new ArrayList<>();
        final Iterator<ParameterMeta> parameterMetasIterator = parameterMetas.iterator();
        for (Type type : parameterTypes) {
            final String      name            = parameterMetasIterator.next().name;
            final JsonElement argumentElement = root.get(name);
            if (argumentElement == null) {
                throw new IllegalStateException(String.format("In json not found argument '%s'.", name));
            }
            arguments.add(gson.fromJson(argumentElement, type));
        }

        return arguments.toArray();
    }

}
