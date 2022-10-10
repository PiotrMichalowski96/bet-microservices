package com.piter.match.api.service;

import com.piter.match.api.domain.Match;
import com.piter.match.api.exception.MatchNotFoundException;
import com.piter.match.api.producer.MatchProducer;
import com.piter.match.api.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MatchService {

  private final MatchRepository matchRepository;
  private final MatchProducer matchProducer;

  public Flux<Match> findAll() {
    return matchRepository.findAll();
  }

  public Flux<Match> findAllByOrderByMatchStartTime() {
    return matchRepository.findAllByOrderByStartTimeDesc();
  }

  public Flux<Match> findAllByOrderByMatchRoundStartTime() {
    return matchRepository.findAllByOrderByRoundStartTimeDesc();
  }

  @Cacheable(value = "match", key = "#id")
  public Mono<Match> findById(Long id) {
    return matchRepository.findById(id)
        .switchIfEmpty(Mono.error(new MatchNotFoundException(id)))
        .cache();
  }

  @CacheEvict(value = "match", key = "#match.id", condition = "#match.id != null")
  public Mono<Match> saveMatch(Match match) {
    return Mono.just(matchProducer.sendSaveMatchEvent(match))
        .then(matchRepository.findOne(Example.of(match)));
  }

  @CacheEvict(value = "match", key = "#id")
  public Mono<Void> deleteMatch(Long id) {
    return matchRepository.findById(id)
        .switchIfEmpty(Mono.error(new MatchNotFoundException(id)))
        .flatMap(match -> {
          matchProducer.sendDeleteMatchEvent(match);
          return Mono.empty();
        });
  }
}
