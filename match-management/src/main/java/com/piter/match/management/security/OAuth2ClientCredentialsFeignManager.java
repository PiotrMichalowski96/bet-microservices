package com.piter.match.management.security;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

@Slf4j
@RequiredArgsConstructor
public class OAuth2ClientCredentialsFeignManager {

  private final OAuth2AuthorizedClientManager manager;
  private final ClientRegistration clientRegistration;

  public Optional<String> getAccessToken() {
    try {
      var request = OAuth2AuthorizeRequest
          .withClientRegistrationId(clientRegistration.getRegistrationId())
          .principal(SecurityContextHolder.getContext().getAuthentication())
          .build();

      return Optional.ofNullable(manager.authorize(request))
          .map(OAuth2AuthorizedClient::getAccessToken)
          .map(OAuth2AccessToken::getTokenValue);
    } catch (Exception e) {
      logger.error("Client credentials error. Message: {}", e.getMessage());
      return Optional.empty();
    }
  }
}
