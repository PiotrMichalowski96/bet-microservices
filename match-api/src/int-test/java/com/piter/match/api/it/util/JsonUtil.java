package com.piter.match.api.it.util;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.testcontainers.shaded.org.apache.commons.io.IOUtils;

@UtilityClass
public class JsonUtil {

  private static final String RESOURCE_PATH = "src/int-test/resources/";
  private static final ObjectMapper OBJECT_MAPPER;

  static {
    OBJECT_MAPPER = new ObjectMapper();
    OBJECT_MAPPER.registerModule(new JavaTimeModule());
    OBJECT_MAPPER.registerModule(new ParameterNamesModule(JsonCreator.Mode.PROPERTIES));
  }

  public static <T> T convertJson(String json, Class<T> clazz) throws JsonProcessingException {
    if (StringUtils.isBlank(json)) {
      return null;
    }
    return OBJECT_MAPPER.readValue(json, clazz);
  }

  public static <T> List<T> convertJsonArray(String json, Class<T> clazz) throws JsonProcessingException {
    if (StringUtils.isBlank(json)) {
      return null;
    }
    CollectionType collectionType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
    return OBJECT_MAPPER.readValue(json, collectionType);
  }

  public static String readFileAsString(String resourceFilePath) throws IOException {
    FileInputStream fis = new FileInputStream(RESOURCE_PATH + resourceFilePath);
    return IOUtils.toString(fis, StandardCharsets.UTF_8);
  }

  public static <T> T readJsonFile(String filePath, Class<T> clazz) throws IOException {
    String json = readFileAsString(filePath);
    return convertJson(json, clazz);
  }

  public static <T> List<T> readJsonArrayFile(String filePath, Class<T> clazz) throws IOException {
    String json = readFileAsString(filePath);
    return convertJsonArray(json, clazz);
  }
}
