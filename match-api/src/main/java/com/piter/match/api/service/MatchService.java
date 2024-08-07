package com.piter.match.api.service;

import static java.util.function.Predicate.not;

import com.piter.api.commons.event.MatchEvent;
import com.piter.api.commons.model.Match;
import com.piter.api.commons.model.MatchResult;
import com.piter.match.api.exception.MatchNotFoundException;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.repository.MatchRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

  public Flux<Match> findAllUpcomingOrderByStartTimeDesc() {
    return matchRepository.findAllByOrderByStartTimeDesc()
        .filter(not(this::isStarted));
  }

  public Flux<Match> findAllUpcomingOrderByStartTimeAsc() {
    return matchRepository.findAllByOrderByStartTimeAsc()
        .filter(not(this::isStarted));
  }

  public Flux<Match> findAllOngoing() {
    return matchRepository.findAll()
        .filter(this::isStarted)
        .filter(this::notFinished);
  }

  private boolean isStarted(Match match) {
    return Optional.ofNullable(match.startTime())
        .map(startTime -> LocalDateTime.now().isAfter(startTime))
        .orElse(false);
  }

  private boolean notFinished(Match match) {
    return Optional.ofNullable(match.result())
        .isEmpty();
  }

  @Cacheable(value = "matches", key = "#id")
  public Mono<Match> findById(Long id) {
    return matchRepository.findById(id)
        .switchIfEmpty(Mono.error(new MatchNotFoundException(id)))
        .cache();
  }

  public Mono<Match> saveMatch(Match match) {
    return sequenceGeneratorService.generateSequenceMatchId(Match.SEQUENCE_NAME)
        .map(id -> mapToMatchWithId(match, id))
        .map(m -> matchProducer.sendSaveMatchEvent(MatchEvent.of(m)).toMatch());
  }

  private Match mapToMatchWithId(Match match, Long id) {
    return Match.builder()
        .id(id)
        .homeTeam(match.homeTeam())
        .awayTeam(match.awayTeam())
        .startTime(match.startTime())
        .result(match.result())
        .round(match.round())
        .build();
  }

  @CacheEvict(value = "matches", key = "#id")
  public Mono<Match> updateMatch(Long id, Match match) {
    return matchRepository.findById(id)
        .switchIfEmpty(Mono.error(new MatchNotFoundException(id)))
        .map(existingMatch -> mapToMatchWithId(match, existingMatch.id()))
        .map(m -> matchProducer.sendSaveMatchEvent(MatchEvent.of(m)).toMatch());
  }

  @CacheEvict(value = "matches", key = "#id")
  public Mono<Match> updateMatchResult(Long id, MatchResult matchResult) {
    return matchRepository.findById(id)
        .switchIfEmpty(Mono.error(new MatchNotFoundException(id)))
        .map(m -> mapToMatchWithResult(m, matchResult))
        .map(m -> matchProducer.sendSaveMatchEvent(MatchEvent.of(m)).toMatch());
  }

  private Match mapToMatchWithResult(Match match, MatchResult matchResult) {
    return Match.builder()
        .id(match.id())
        .homeTeam(match.homeTeam())
        .awayTeam(match.awayTeam())
        .startTime(match.startTime())
        .result(matchResult)
        .round(match.round())
        .build();
  }

  public Mono<Void> deleteMatch(Long id) {
    return matchRepository.findById(id)
        .switchIfEmpty(Mono.error(new MatchNotFoundException(id)))
        .flatMap(match -> {
          matchProducer.sendDeleteMatchEvent(MatchEvent.of(match));
          return Mono.empty();
        });
  }
}
