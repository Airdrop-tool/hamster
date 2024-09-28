package com.cuongpq.hamster.utils;

import com.cuongpq.hamster.dto.UserDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.TimeZone;

@Slf4j
public class CommonUtil {

    public static final ObjectMapper MAPPER = getObjectMapper();

    private static ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setTimeZone(TimeZone.getDefault());
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Ignore null properties
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignore non exist properties
        return mapper;
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return (T) MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            log.error("Lỗi khi convert json: " + e.getMessage());
            return null;
        }
    }

    public static String toJson(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    public static String tma(String queryId) {
        return String.format("tma %s", queryId);
    }

    public static UserDto getUserFromQueryId(String queryId) {
        String decode = URLDecoder.decode(queryId, StandardCharsets.UTF_8);
        String[] arr = decode.split("&");
        UserDto user = new UserDto();
        for (String value : arr) {
            if (value.startsWith("user=")) {
                user = fromJson(value.split("=")[1], UserDto.class);
                break;
            }
        }
        return user;
    }
}
