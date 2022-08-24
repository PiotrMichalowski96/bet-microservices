package com.piter.match.api.exception;

public class MatchKafkaException extends RuntimeException{

  public MatchKafkaException(String message) {
    super(message);
  }

  public static MatchKafkaException eventTypeNotFound() {
    return new MatchKafkaException("Cannot find event type of message.");
  }
}
