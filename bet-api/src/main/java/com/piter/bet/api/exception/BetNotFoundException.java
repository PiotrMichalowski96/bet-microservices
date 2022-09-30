package com.piter.bet.api.exception;

public class BetNotFoundException extends RuntimeException {

  public BetNotFoundException(Long id) {
    super("Bet not found with id: " + id);
  }
}
