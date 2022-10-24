package com.piter.bet.event.aggregator.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bet.event.aggregator.domain.Bet;
import com.piter.bet.event.aggregator.domain.Match;
import com.piter.bet.event.aggregator.domain.User;
import org.junit.jupiter.api.Test;

class BetIdGeneratorTest {

  @Test
  void shouldGenerateBetId() {
    //given
    var user = User.builder()
        .firstName("Piotr")
        .lastName("M")
        .nickname("piter")
        .build();
    var matchId = 123L;

    var bet = Bet.builder()
        .user(user)
        .match(Match.builder()
            .id(matchId)
            .build())
        .build();

    var expectedBetId = "943ff7c2-085b-3fb5-9216-cd18f4b81358";

    //when
    var betIdGenerator = new BetIdGenerator(bet);
    String betId = betIdGenerator.generateId();

    //then
    assertThat(betId).isEqualTo(expectedBetId);
  }
}