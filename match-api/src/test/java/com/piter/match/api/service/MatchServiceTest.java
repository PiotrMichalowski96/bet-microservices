package com.piter.match.api.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.match.api.config.MatchApiTestConfig;
import com.piter.match.api.domain.Match;
import com.piter.match.api.domain.MatchResult;
import com.piter.match.api.domain.MatchRound;
import com.piter.match.api.repository.MatchRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

@DataMongoTest
@ActiveProfiles("TEST")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = MatchApiTestConfig.class)
class MatchServiceTest {

  @Autowired
  private MatchRepository matchRepository;

  @Autowired
  private MatchService matchService;

  @BeforeEach
  void initData() {
    matchRepository.deleteAll();
    List<Match> matches = List.of(
        Match.builder()
            .id(1L)
            .homeTeam("FC Barcelona")
            .awayTeam("Real Madrid")
            .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
            .result(new MatchResult(4, 0))
            .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
            .build(),
        Match.builder()
            .id(2L)
            .homeTeam("Sevilla")
            .awayTeam("Athletic Bilbao")
            .startTime(LocalDateTime.of(2022, 2, 8, 19, 0, 0))
            .result(new MatchResult(2, 1))
            .round(new MatchRound("LaLiga round 29", LocalDateTime.of(2022, 2, 7, 21, 0, 0)))
            .build(),
        Match.builder()
            .id(3L)
            .homeTeam("Atletico Madrid")
            .awayTeam("Valencia")
            .startTime(LocalDateTime.of(2022, 2, 15, 21, 0, 0))
            .result(new MatchResult(2, 2))
            .round(new MatchRound("LaLiga round 30", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
            .build()
    );
    matchRepository.saveAll(matches);
  }

  @Test
  void shouldGetMatchesWithoutOrder() {
    Mono<List<Match>> matchListMono = matchService.findAll();

    matchListMono.subscribe(matches -> {
      assertThat(matches.get(0).getId()).isEqualTo(1L);
      assertThat(matches.get(1).getId()).isEqualTo(2L);
      assertThat(matches.get(2).getId()).isEqualTo(3L);
    });
  }

  @Test
  void shouldGetMatchesOrderedByRoundTime() {
    Mono<List<Match>> matchListMono = matchService.findAllByOrderByMatchRoundStartTime();

    matchListMono.subscribe(matches -> {
      assertThat(matches.get(0).getId()).isEqualTo(1L);
      assertThat(matches.get(1).getId()).isEqualTo(3L);
      assertThat(matches.get(2).getId()).isEqualTo(2L);
    });
  }

  @Test
  void shouldGetMatchesOrderedByMatchTime() {
    Mono<List<Match>> matchListMono = matchService.findAllByOrderByMatchStartTime();

    matchListMono.subscribe(matches -> {
      assertThat(matches.get(0).getId()).isEqualTo(1L);
      assertThat(matches.get(1).getId()).isEqualTo(3L);
      assertThat(matches.get(2).getId()).isEqualTo(2L);
    });
  }

  @Test
  void shouldGetMatchById() {
    var id = 2L;
    Mono<Match> matchMono = matchService.findById(id);

    matchMono.subscribe(match -> assertThat(match.getId()).isEqualTo(id));
  }
}