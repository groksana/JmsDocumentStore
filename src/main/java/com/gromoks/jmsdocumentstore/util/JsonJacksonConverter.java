package com.gromoks.jmsdocumentstore.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JsonJacksonConverter {
    private static ObjectMapper objectMapper = new ObjectMapper();

    final static Logger log = LoggerFactory.getLogger(JsonJacksonConverter.class);

    public static <T> T parseValue(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <K> String toJson(K list) {
        try {
            return objectMapper.writeValueAsString(list);
        } catch (JsonProcessingException e) {
            log.error("Error in Json converter", e);
            throw new RuntimeException("Error in Json convert", e);
        }
    }
}
