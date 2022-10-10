package com.piter.bet.api.config;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DefaultOAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.core.OAuth2AuthenticatedPrincipal;
import org.springframework.security.oauth2.server.resource.introspection.ReactiveOpaqueTokenIntrospector;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class KeycloakReactiveTokenInstrospector implements ReactiveOpaqueTokenIntrospector {

  private static final String REALM_ACCESS = "realm_access";
  private static final String ROLES = "roles";

  private final ReactiveOpaqueTokenIntrospector delegate;

  @Override
  public Mono<OAuth2AuthenticatedPrincipal> introspect(String token) {
    return delegate.introspect(token)
        .map( this::mapPrincipal);
  }

  protected OAuth2AuthenticatedPrincipal mapPrincipal(OAuth2AuthenticatedPrincipal principal) {

    return new DefaultOAuth2AuthenticatedPrincipal(
        principal.getName(),
        principal.getAttributes(),
        extractAuthorities(principal));
  }

  protected Collection<GrantedAuthority> extractAuthorities(OAuth2AuthenticatedPrincipal principal) {

    Map<String, List<String>> realm_access = principal.getAttribute(REALM_ACCESS);
    List<String> roles = realm_access.getOrDefault(ROLES, Collections.emptyList());
    List<GrantedAuthority> rolesAuthorities = roles.stream()
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    Set<GrantedAuthority> allAuthorities = new HashSet<>();
    allAuthorities.addAll(principal.getAuthorities());
    allAuthorities.addAll(rolesAuthorities);

    return allAuthorities;
  }
}
