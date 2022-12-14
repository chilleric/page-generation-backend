package com.chillleric.page_generation.service;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.constant.ResponseType;
import com.chillleric.page_generation.exception.InvalidRequestException;
import com.chillleric.page_generation.log.AppLogger;
import com.chillleric.page_generation.log.LoggerFactory;
import com.chillleric.page_generation.log.LoggerType;
import com.chillleric.page_generation.utils.ObjectValidator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class AbstractService<r> {

  @Autowired
  protected r repository;

  @Autowired
  protected Environment env;

  @Autowired
  protected ObjectValidator objectValidator;

  protected ObjectMapper objectMapper;

  protected AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);

  @PostConstruct
  public void init() {
    objectMapper =
        new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  protected String generateParamsValue(List<String> list) {
    StringBuilder result = new StringBuilder();
    for (int i = 0; i < list.size(); i++) {
      result.append(list.get(i));
      if (i != list.size() - 1) {
        result.append(",");
      }
    }
    return result.toString();
  }

  protected BCryptPasswordEncoder bCryptPasswordEncoder() {
    return new BCryptPasswordEncoder();
  }

  protected <T> void validate(T request) {
    boolean isError = false;
    Map<String, String> errors = objectValidator
        .validateRequestThenReturnMessage(generateError(request.getClass()), request);
    for (Map.Entry<String, String> items : errors.entrySet()) {
      if (items.getValue().length() > 0) {
        isError = true;
        break;
      }
    }
    if (isError) {
      throw new InvalidRequestException(errors, LanguageMessageKey.INVALID_REQUEST);
    }
  }

  protected Map<String, String> generateError(Class<?> clazz) {
    Field[] fields = clazz.getDeclaredFields();
    Map<String, String> result = new HashMap<>();
    for (Field field : fields) {
      result.put(field.getName(), "");
    }
    return result;
  }

  protected ResponseType isPublic(String ownerId, String loginId, boolean skipAccessability) {
    if (skipAccessability) {
      return ResponseType.PRIVATE;
    }
    if (ownerId.compareTo(loginId) == 0) {
      return ResponseType.PRIVATE;
    } else {
      return ResponseType.PUBLIC;
    }
  }
}
