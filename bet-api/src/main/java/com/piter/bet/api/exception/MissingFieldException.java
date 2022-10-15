package com.piter.bet.api.exception;

import com.piter.api.commons.domain.Bet;

public class MissingFieldException extends RuntimeException {

  public MissingFieldException(String message, Bet bet) {
    super(message + "Bet: " + bet.toString());
  }
}
