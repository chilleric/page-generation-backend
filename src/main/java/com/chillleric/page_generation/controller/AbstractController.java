package com.chillleric.page_generation.controller;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import com.chillleric.page_generation.constant.LanguageMessageKey;
import com.chillleric.page_generation.dto.common.CommonResponse;
import com.chillleric.page_generation.dto.common.ValidationResult;
import com.chillleric.page_generation.exception.BadSqlException;
import com.chillleric.page_generation.exception.UnauthorizedException;
import com.chillleric.page_generation.inventory.user.UserInventory;
import com.chillleric.page_generation.jwt.JwtValidation;
import com.chillleric.page_generation.jwt.TokenContent;
import com.chillleric.page_generation.log.AppLogger;
import com.chillleric.page_generation.log.LoggerFactory;
import com.chillleric.page_generation.log.LoggerType;
import com.chillleric.page_generation.repository.user.User;

public abstract class AbstractController<s> {

  @Autowired
  protected s service;

  @Autowired
  protected JwtValidation jwtValidation;

  @Autowired
  protected UserInventory userInventory;

  protected AppLogger APP_LOGGER = LoggerFactory.getLogger(LoggerType.APPLICATION);

  protected ValidationResult validateToken(HttpServletRequest request) {
    String token = jwtValidation.getJwtFromRequest(request);
    if (token == null) {
      throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
    }
    return checkAuthentication(token);
  }

  protected ValidationResult validateSSE(String token) {
    if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
      return checkAuthentication(token.substring(7));
    } else {
      throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
    }

  }

  protected ValidationResult checkAuthentication(String token) {
    TokenContent info = jwtValidation.getUserIdFromJwt(token);
    User user = userInventory.getActiveUserById(info.getUserId())
        .orElseThrow(() -> new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED));
    if (!user.getTokens().containsKey(info.getDeviceId())) {
      APP_LOGGER.error("not found deviceid authen");
      throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
    }
    Date now = new Date();
    if (user.getTokens().get(info.getDeviceId()).compareTo(now) <= 0) {
      APP_LOGGER.error("not found expired device authen");
      throw new UnauthorizedException(LanguageMessageKey.UNAUTHORIZED);
    }
    return new ValidationResult(user.get_id().toString());

  }

  protected <T> ResponseEntity<CommonResponse<T>> response(Optional<T> response,
      String successMessage) {
    return new ResponseEntity<>(
        new CommonResponse<>(true, response.get(), successMessage, HttpStatus.OK.value()),
        HttpStatus.OK);
  }

  protected <T> T filterResponse(T input) {
    for (Field field : input.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        boolean isExist = false;

        if (!isExist) {
          if (field.getType() == String.class) {
            field.set(input, "");
          }
          if (field.getType() == int.class) {
            field.set(input, 0);
          }
          if (field.getType() == boolean.class) {
            field.set(input, false);
          }
          if (field.getType() == Map.class) {
            field.set(input, new HashMap<>());
          }
          if (field.getType() == List.class) {
            field.set(input, new ArrayList<>());
          }
        }
      } catch (Exception e) {
        throw new BadSqlException(LanguageMessageKey.SERVER_ERROR);
      }
    }
    return input;
  }
}
