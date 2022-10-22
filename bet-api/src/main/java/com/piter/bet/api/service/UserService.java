package com.piter.bet.api.service;

import com.piter.bet.api.repository.BetRepository;
import com.piter.bet.api.model.UserResultProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class UserService {

  private final BetRepository betRepository;

  public Flux<UserResultProjection> findAllUserResults() {
    return betRepository.findAllUsersResults();
  }

  public Mono<UserResultProjection> findUserResultByNickname(String nickname) {
    return null;
  }
}
