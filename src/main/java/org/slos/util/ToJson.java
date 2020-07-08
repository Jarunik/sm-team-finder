package org.slos.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface ToJson {

    Logger logger = LoggerFactory.getLogger(ToJson.class);

    default String toJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);

        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            System.out.println("Failed to return as JSON: " + this);
            logger.error("Failed to return as JSON: " + this);
            System.out.println("Reason: " + e);
            logger.error("Reason: ", e);
            return null;
        }
    }
}
