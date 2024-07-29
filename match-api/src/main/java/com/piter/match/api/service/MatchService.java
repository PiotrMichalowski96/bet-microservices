package com.piter.match.api.service;

import com.piter.api.commons.event.MatchEvent;
import com.piter.api.commons.model.Match;
import com.piter.api.commons.model.MatchResult;
import com.piter.match.api.exception.MatchNotFoundException;
import com.piter.match.api.producer.MatchKafkaProducer;
import com.piter.match.api.repository.MatchRepository;
import com.piter.match.api.web.RequestParams;
import com.piter.match.api.web.RequestParams.Order;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class MatchService {

  private final MatchRepository matchRepository;
  private final MatchKafkaProducer matchProducer;
  private final SequenceGeneratorService sequenceGeneratorService;
  private final Map<Order, Sort> sortMap = Map.of(
      Order.MATCH_TIME_ASC, Sort.by("startTime").ascending(),
      Order.MATCH_TIME_DESC, Sort.by("startTime").descending(),
      Order.ROUND_TIME_ASC, Sort.by("round.startTime").ascending(),
      Order.ROUND_TIME_DESC, Sort.by("round.startTime").descending()
  );

  public Flux<Match> findAllBy(RequestParams requestParams) {
    Pageable page = createPageFrom(requestParams);
    return matchRepository.findAllBy(page);
  }

  private Pageable createPageFrom(RequestParams requestParams) {
    Sort sort = sortMap.get(requestParams.order());
    return PageRequest.of(requestParams.page(), requestParams.size(), sort);
  }

  public Flux<Match> findAllUpcomingBy(RequestParams requestParams) {
    Pageable page = createPageFrom(requestParams);
    return matchRepository.findByStartTimeAfter(LocalDateTime.now(), page);
  }

  public Flux<Match> findAllOngoingBy(RequestParams requestParams) {
    Pageable page = createPageFrom(requestParams);
    return matchRepository.findByStartTimeBeforeAndResultIsNull(LocalDateTime.now(), page);
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
