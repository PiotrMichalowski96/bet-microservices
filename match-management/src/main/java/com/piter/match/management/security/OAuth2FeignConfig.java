package com.piter.match.management.security;

import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

@Configuration
@RequiredArgsConstructor
public class OAuth2FeignConfig {

  public static final String CLIENT_REGISTRATION_ID = "match-management";
  private static final String AUTH_HEADER_TEMPLATE = "Bearer %s";

  private final ClientRegistrationRepository clientRegistrationRepository;
  private final OAuth2AuthorizedClientService oAuth2AuthorizedClientService;

  @Bean
  public OAuth2AuthorizedClientManager authorizedClientManager() {
    var authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder()
        .clientCredentials()
        .refreshToken()
        .build();
    var authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(clientRegistrationRepository, oAuth2AuthorizedClientService);
    authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
    return authorizedClientManager;
  }

  @Bean
  public RequestInterceptor requestInterceptor(OAuth2AuthorizedClientManager authorizedClientManager) {
    ClientRegistration clientRegistration = clientRegistrationRepository.findByRegistrationId(CLIENT_REGISTRATION_ID);
    var clientCredentialsFeignManager = new OAuth2ClientCredentialsFeignManager(authorizedClientManager, clientRegistration);
    return requestTemplate -> requestTemplate.header(HttpHeaders.AUTHORIZATION, getAuthHeader(clientCredentialsFeignManager));
  }

  private String getAuthHeader(OAuth2ClientCredentialsFeignManager clientCredentialsFeignManager) {
    return clientCredentialsFeignManager.getAccessToken()
        .map(token -> String.format(AUTH_HEADER_TEMPLATE, token))
        .orElseThrow(OAuth2AccessTokenRetrievalException::retrieveTokenFailure);
  }
}
