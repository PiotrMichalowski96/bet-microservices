package com.piter.bet.api.web;

import com.piter.bet.api.service.UserService;
import com.piter.bet.api.model.UserResultProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/bets/results")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/users")
  Flux<UserResultProjection> findAll() {
    return userService.findAllUserResults();
  }

  @GetMapping("/users/{nickname}")
  Mono<UserResultProjection> findUserResultByNickname(@PathVariable("nickname") String nickname) {
    return userService.findUserResultByNickname(nickname);
  }
}
