package com.piter.bet.api.service;

import static com.piter.bet.api.util.BetTestData.createBetList;
import static com.piter.bet.api.util.UserTestData.createFirstUser;
import static com.piter.bet.api.util.UserTestData.createFourthUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.piter.api.commons.domain.Bet;
import com.piter.api.commons.domain.User;
import com.piter.bet.api.exception.BetNotFoundException;
import com.piter.bet.api.producer.BetKafkaProducer;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class BetServiceTest extends AbstractServiceTest {

  private final BetService betService;

  @MockBean
  private BetKafkaProducer betKafkaProducer;

  @Autowired
  public BetServiceTest(BetService betService) {
    super(createBetList());
    this.betService = betService;
  }

  @Test
  void shouldGetAllVisibleBets() {
    //given
    var user = super.getBets().getFirst().user();

    //when
    Flux<Bet> betFlux = betService.findAll(user);

    //then
    StepVerifier.create(betFlux)
        .assertNext(bet -> assertThat(bet.id()).isEqualTo("1"))
        .assertNext(bet -> assertThat(bet.id()).isEqualTo("3"))
        .verifyComplete();
  }

  @Test
  void shouldGetAllVisibleBetsOrderedByMatchStartTimeAsc() {
    //given
    var user = super.getBets().getFirst().user();

    //when
    Flux<Bet> betFlux = betService.findAllByOrderByMatchStartTimeAsc(user);

    //then
    StepVerifier.create(betFlux)
        .assertNext(bet -> assertThat(bet.id()).isEqualTo("3"))
        .assertNext(bet -> assertThat(bet.id()).isEqualTo("1"))
        .verifyComplete();
  }

  @Test
  void shouldGetAllVisibleBetsOrderedByMatchStartTimeDesc() {
    //given
    var user = super.getBets().getFirst().user();

    //when
    Flux<Bet> betFlux = betService.findAllByOrderByMatchStartTimeDesc(user);

    //then
    StepVerifier.create(betFlux)
        .assertNext(bet -> assertThat(bet.id()).isEqualTo("1"))
        .assertNext(bet -> assertThat(bet.id()).isEqualTo("3"))
        .verifyComplete();
  }

  @Test
  void shouldGetAllVisibleBetsByMatchId() {
    //given
    var matchId = 1L;
    var user = super.getBets().getFirst().user();

    //when
    Flux<Bet> betFlux = betService.findAllByMatchId(matchId, user);

    //then
    StepVerifier.create(betFlux)
        .assertNext(bet -> assertThat(bet.id()).isEqualTo("1"))
        .verifyComplete();
  }

  @Test
  void shouldGetAllVisibleBetsByUserNickname() {
    //given
    var nickname = super.getBets().get(2).user().nickname();
    var user = super.getBets().get(0).user();

    //when
    Flux<Bet> betFlux = betService.findAllByUserNickname(nickname, user);

    //then
    StepVerifier.create(betFlux)
        .assertNext(bet -> assertThat(bet.id()).isEqualTo("3"))
        .verifyComplete();
  }

  @Test
  void shouldGetBetsWhenMatchIsStarted() {
    //given
    var user = createFourthUser();

    //when
    Flux<Bet> betFlux = betService.findAll(user);

    //then
    StepVerifier.create(betFlux)
        .expectNextCount(1)
        .verifyComplete();
  }

  @Test
  void shouldGetBetById() {
    //given
    var id = "2";
    var user = super.getBets().get(1).user();

    //when
    Mono<Bet> betMono = betService.findById(id, user);

    //then
    StepVerifier.create(betMono)
        .assertNext(bet -> assertThat(bet.id()).isEqualTo(id))
        .verifyComplete();
  }

  @Test
  void shouldNotGetBetByIdBecauseUserIsNotOwner() {
    //given
    var id = "2";
    var user = createFourthUser();

    //when
    Mono<Bet> betMono = betService.findById(id, user);

    //then
    StepVerifier.create(betMono)
        .expectError(BetNotFoundException.class)
        .verify();
  }

  @Test
  void shouldSaveBet() {
    //given
    var id = "99";
    var bet = Bet.builder()
        .id(id)
        .build();

    when(betKafkaProducer.sendSaveBetEvent(bet)).thenReturn(bet);

    //when
    Mono<Bet> responseBetMono = betService.saveBet(bet);

    //then
    StepVerifier.create(responseBetMono)
        .assertNext(responseBet -> assertThat(responseBet.id()).isEqualTo(id))
        .verifyComplete();
  }

  @Test
  void shouldDeleteBet() {
    //given
    var id = "99";
    var user = createFourthUser();
    var bet = Bet.builder()
        .id(id)
        .user(user)
        .build();

    super.saveBet(bet);

    //when
    betService.deleteBet(id, user).block();

    //then
    Mono<Bet> betMono = super.findBetBy(id);
    StepVerifier.create(betMono)
        .expectNextCount(0)
        .verifyComplete();
  }

  @ParameterizedTest
  @MethodSource("provideBetsAndUsers")
  void shouldDeleteBetThrowBetNotFoundError(String id, Bet bet, User user) {
    //given
    super.saveBet(bet);

    //when
    Mono<Void> deleteBetMono = betService.deleteBet(id, user);

    //then
    StepVerifier.create(deleteBetMono)
        .expectError(BetNotFoundException.class)
        .verify();

    super.deleteBet(bet); //cleaning after test
  }

  private static Stream<Arguments> provideBetsAndUsers() {
    var id = "123";
    var wrongId = "987";

    var userOwner = createFourthUser();
    var wrongUser = createFirstUser();

    var bet = Bet.builder()
        .id(id)
        .user(userOwner)
        .build();

    return Stream.of(
        Arguments.of(wrongId, bet,userOwner),
        Arguments.of(id, bet, wrongUser)
    );
  }
}