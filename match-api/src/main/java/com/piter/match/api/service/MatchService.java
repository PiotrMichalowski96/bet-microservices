package com.piter.match.api.service;

import com.piter.api.commons.domain.Match;
import com.piter.match.api.exception.MatchNotFoundException;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.repository.MatchRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
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
        .filter(this::isNotStarted);
  }

  public Flux<Match> findAllUpcomingOrderByStartTimeAsc() {
    return matchRepository.findAllByOrderByStartTimeAsc()
        .filter(this::isNotStarted);
  }

  private boolean isNotStarted(Match match) {
    return Optional.ofNullable(match.startTime())
        .map(startTime -> LocalDateTime.now().isBefore(startTime))
        .orElse(false);
  }

  public Mono<Match> findById(Long id) {
    return matchRepository.findById(id)
        .switchIfEmpty(Mono.error(new MatchNotFoundException(id)))
        .cache();
  }

  public Mono<Match> saveMatch(Match match) {
    if (match.id() != null) {
      return Mono.just(matchProducer.sendSaveMatchEvent(match));
    }
    return sequenceGeneratorService.generateSequenceMatchId(Match.SEQUENCE_NAME)
        .map(id -> mapToMatchWithId(match, id))
        .map(matchProducer::sendSaveMatchEvent);
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

//  @CacheEvict(value = "match", key = "#id")
  public Mono<Void> deleteMatch(Long id) {
    return matchRepository.findById(id)
        .switchIfEmpty(Mono.error(new MatchNotFoundException(id)))
        .flatMap(match -> {
          matchProducer.sendDeleteMatchEvent(match);
          return Mono.empty();
        });
  }
}
