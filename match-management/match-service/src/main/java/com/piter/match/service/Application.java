package com.piter.match.service;

import com.piter.match.service.domain.Match;
import com.piter.match.service.domain.MatchResult;
import com.piter.match.service.domain.MatchRound;
import com.piter.match.service.producer.MatchProducer;
import com.piter.match.service.rest.HandledMatchService;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@Slf4j
@EnableFeignClients
@RequiredArgsConstructor
@SpringBootApplication
public class Application implements CommandLineRunner {

  private final MatchProducer matchProducer;
  private final HandledMatchService matchHandler;

  public static void main(String[] args) {
    SpringApplication.run(Application.class, args);
  }

  @Override
  public void run(String... args) throws Exception {

//    testMatchProducer();

    testMatchHandler();
    testListMatchHandler("");
    testListMatchHandler("round-time");
    testListMatchHandler("match-time");
  }

  private void testMatchProducer() {
    Match match = Match.builder()
        .id(1L)
        .homeTeam("FC Barcelona")
        .awayTeam("PSG")
        .startTime(LocalDateTime.of(2022, 2, 17, 21, 0, 0))
        .result(new MatchResult(3, 1))
        .round(new MatchRound("Champions League Semi-Finals", LocalDateTime.of(2022, 2, 14, 21, 0, 0)))
        .build();

    matchProducer.sendMatch(match);
  }

  private void testMatchHandler() {
    matchHandler.getMatch(1L)
        .ifPresentOrElse(match -> logger.info("Retrieved match: {}", match),
            () -> logger.info("Cannot find match by id = 1"));
  }

  private void testListMatchHandler(String order) {
    matchHandler.getMatchList(order)
        .forEach(match -> logger.info("Order: {}, Retrieved match list: {}", order, match));
  }
}
