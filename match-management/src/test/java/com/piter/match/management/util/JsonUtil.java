package com.piter.match.management.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.apache.commons.io.IOUtils;

@UtilityClass
public class JsonUtil {

  private static final String RESOURCE_PATH = "src/test/resources/";
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
  private static final ObjectMapper OBJECT_MAPPER;

  static {
    OBJECT_MAPPER = new ObjectMapper();
    OBJECT_MAPPER.registerModule(new JavaTimeModule());
    OBJECT_MAPPER.setDateFormat(DATE_FORMAT);
  }

  public static String readFileAsString(String resourceFilePath) throws IOException {
    var fis = new FileInputStream(RESOURCE_PATH + resourceFilePath);
    return IOUtils.toString(fis);
  }

  public static <T> T readJsonFile(String filePath, Class<T> clazz) throws IOException {
    var json = readFileAsString(filePath);
    return convertJson(json, clazz);
  }

  private static <T> T convertJson(String json, Class<T> clazz) throws JsonProcessingException {
    if (json == null || json.isEmpty()) {
      return null;
    }
    return OBJECT_MAPPER.readValue(json, clazz);
  }

  public static <T> List<T> readJsonArrayFile(String filePath, Class<T> clazz) throws IOException {
    var json = readFileAsString(filePath);
    return convertJsonArray(json, clazz);
  }

  private static <T> List<T> convertJsonArray(String json, Class<T> clazz) throws JsonProcessingException {
    if (json == null || json.isEmpty()) {
      return null;
    }
    CollectionType collectionType = OBJECT_MAPPER.getTypeFactory().constructCollectionType(List.class, clazz);
    return OBJECT_MAPPER.readValue(json, collectionType);
  }
}
