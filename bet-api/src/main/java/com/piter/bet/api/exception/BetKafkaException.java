package com.piter.bet.api.exception;

public class BetKafkaException extends RuntimeException{

  public BetKafkaException(String message) {
    super(message);
  }

  public static BetKafkaException eventTypeNotFound() {
    return new BetKafkaException("Cannot find event type of message.");
  }
}
