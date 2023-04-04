package com.piter.match.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class MatchApiExceptionHandler {

  @ExceptionHandler(MatchNotFoundException.class)
  ResponseEntity<Object> betNotFound(MatchNotFoundException ex) {
    logger.debug("Handling exception: {}", ex.toString());
    return ResponseEntity.notFound().build();
  }
}
