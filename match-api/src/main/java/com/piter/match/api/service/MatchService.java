package com.piter.match.api.service;

import com.piter.api.commons.domain.Match;
import com.piter.match.api.exception.MatchNotFoundException;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.repository.MatchRepository;
import java.time.LocalDateTime;
import java.util.Optional;
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
  private final MatchKafkaProducer matchProducer;
  private final SequenceGeneratorService sequenceGeneratorService;

  public Flux<Match> findAll() {
    return matchRepository.findAll();
  }

  public Flux<Match> findAllByOrderByMatchStartTime() {
    return matchRepository.findAllByOrderByStartTimeDesc();
  }

  public Flux<Match> findAllByOrderByMatchRoundStartTime() {
    return matchRepository.findAllByOrderByRoundStartTimeDesc();
  }

  public Flux<Match> findAllUpcoming() {
    return matchRepository.findAllByOrderByStartTimeDesc()
        .filter(this::isNotStarted);
  }

  private boolean isNotStarted(Match match) {
    return Optional.ofNullable(match.getStartTime())
        .map(startTime -> LocalDateTime.now().isBefore(startTime))
        .orElse(false);
  }

  @Cacheable(value = "match", key = "#id")
  public Mono<Match> findById(Long id) {
    return matchRepository.findById(id)
        .switchIfEmpty(Mono.error(new MatchNotFoundException(id)))
        .cache();
  }

  @CacheEvict(value = "match", key = "#match.id", condition = "#match.id != null")
  public Mono<Match> saveMatch(Match match) {
    if (match.getId() != null) {
      return Mono.just(matchProducer.sendSaveMatchEvent(match))
          .then(matchRepository.findOne(Example.of(match)));
    }
    return sequenceGeneratorService.generateSequenceMatchId(Match.SEQUENCE_NAME)
        .map(id -> mapToMatchWithId(match, id))
        .map(matchProducer::sendSaveMatchEvent)
        .then(matchRepository.findOne(Example.of(match)));
  }

  private Match mapToMatchWithId(Match match, Long id) {
    return Match.builder()
        .id(id)
        .homeTeam(match.getHomeTeam())
        .awayTeam(match.getAwayTeam())
        .startTime(match.getStartTime())
        .result(match.getResult())
        .round(match.getRound())
        .build();
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
