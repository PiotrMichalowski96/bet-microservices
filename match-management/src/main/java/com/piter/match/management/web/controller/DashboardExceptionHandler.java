package com.piter.match.management.web.controller;

import com.piter.match.management.security.OAuth2AccessTokenRetrievalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class DashboardExceptionHandler {

  @ExceptionHandler(OAuth2AccessTokenRetrievalException.class)
  String accessTokenExceptionHandler(OAuth2AccessTokenRetrievalException ex) {
    logger.debug("Access token retrieval exception: {}", ex.toString());
    return "redirect:/login";
  }
}
