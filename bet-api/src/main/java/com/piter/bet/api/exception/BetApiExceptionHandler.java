package com.piter.bet.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class BetApiExceptionHandler {

  @ExceptionHandler(BetNotFoundException.class)
  ResponseEntity<?> betNotFound(BetNotFoundException ex) {
    logger.debug("Handling exception: {}", ex.toString());
    return ResponseEntity.notFound().build();
  }
}
