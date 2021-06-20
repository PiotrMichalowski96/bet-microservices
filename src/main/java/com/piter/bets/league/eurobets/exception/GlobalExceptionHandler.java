package com.piter.bets.league.eurobets.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(value = {
      BetNotFoundException.class,
      BettingRulesException.class,
      MatchNotFoundException.class,
      MatchRoundNotFoundException.class,
      UserNotFoundException.class,
      MethodArgumentTypeMismatchException.class,
      MissingServletRequestParameterException.class,
      MethodArgumentNotValidException.class
  })
  public ResponseEntity<ApiErrorResponse> handleException(Exception exc) {
    ApiErrorResponse errorResponse = new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        exc.getLocalizedMessage()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
  }
}