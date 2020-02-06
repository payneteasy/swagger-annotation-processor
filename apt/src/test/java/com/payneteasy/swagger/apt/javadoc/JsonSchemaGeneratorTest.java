package com.payneteasy.swagger.apt.javadoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.victools.jsonschema.generator.Option;
import com.github.victools.jsonschema.generator.OptionPreset;
import com.github.victools.jsonschema.generator.SchemaGenerator;
import com.github.victools.jsonschema.generator.SchemaGeneratorConfigBuilder;
import com.kjetland.jackson.jsonSchema.JsonSchemaGenerator;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Date;
import java.util.List;

/**
 * @author dvponomarev, 05.02.2020
 */
@Ignore("This is a comparison of two json schema generation libraries.")
public class JsonSchemaGeneratorTest {

    @Test
    public void testKjetland() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        final JsonSchemaGenerator generator = new JsonSchemaGenerator(objectMapper);

        final JsonNode jsonNode = generator.generateJsonSchema(AModel.class);
        System.out.println(jsonNode);
    }

    @Test
    public void testVictools() {
        final ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        final SchemaGenerator generator = new SchemaGenerator(
                new SchemaGeneratorConfigBuilder(objectMapper, OptionPreset.PLAIN_JSON).
                        with(Option.DEFINITIONS_FOR_ALL_OBJECTS).
                        without(Option.VALUES_FROM_CONSTANT_FIELDS).build()
        );

        final JsonNode jsonSchema = generator.generateSchema(AModel.class);
        System.out.println(jsonSchema);
    }

    private static class AModel {
        public static final  String PUBLIC_SF  = "ABC";
        private static final String PRIVATE_SF = "123";

        public  ASubModel    subModel;
        public  int          intValue;
        private String       stringValue;
        public  Date         dateValue;
        public  List<String> stringList;

        public String getStringValue() {
            return stringValue;
        }

        public void setStringValue(String stringValue) {
            this.stringValue = stringValue;
        }
    }

    private static class ASubModel {
        public byte byteValue;
    }

}
