package com.piter.bet.api.web;

import com.piter.api.commons.domain.User;
import com.piter.bet.api.service.UserService;
import com.piter.bet.api.model.UserResultProjection;
import com.piter.bet.api.util.TokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/users-results")
  Flux<UserResultProjection> findAll() {
    return userService.findAllUserResults();
  }

  @GetMapping("/users-results/{nickname}")
  Mono<UserResultProjection> findUserResultByNickname(@PathVariable("nickname") String nickname) {
    return userService.findUserResultByNickname(nickname);
  }

  @GetMapping("/users/current")
  Mono<User> findCurrentUser(BearerTokenAuthentication token) {
    return Mono.just(TokenUtil.getUserFrom(token));
  }
}
