package com.piter.match.management.security;

public class OAuth2AccessTokenRetrievalException extends RuntimeException {

  public OAuth2AccessTokenRetrievalException(String message) {
    super(message);
  }

  public static OAuth2AccessTokenRetrievalException retrieveTokenFailure() {
    return new OAuth2AccessTokenRetrievalException("Failed to retrieve token");
  }
}
