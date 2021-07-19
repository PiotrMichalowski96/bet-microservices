package com.piter.bets.league.eurobets.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.piter.bets.league.eurobets.dto.BetDTO;
import com.piter.bets.league.eurobets.entity.Bet;
import com.piter.bets.league.eurobets.entity.Match;
import com.piter.bets.league.eurobets.entity.MatchRound;
import com.piter.bets.league.eurobets.entity.User;
import com.piter.bets.league.eurobets.exception.BetNotFoundException;
import com.piter.bets.league.eurobets.exception.BettingRulesException;
import com.piter.bets.league.eurobets.exception.MatchNotFoundException;
import com.piter.bets.league.eurobets.exception.UnauthorizedUserException;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import com.piter.bets.league.eurobets.mapper.BetMapper;
import com.piter.bets.league.eurobets.mapper.BetMapperImpl;
import com.piter.bets.league.eurobets.repository.BetRepository;
import com.piter.bets.league.eurobets.repository.MatchRepository;
import com.piter.bets.league.eurobets.repository.UserRepository;
import com.piter.bets.league.eurobets.util.TestDataUtils;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BetServiceTest {

  @InjectMocks
  private BetServiceImpl betService;

  @Mock
  private BetRepository betRepository;

  @Mock
  private MatchRepository matchRepository;

  @Mock
  private UserRepository userRepository;

  @Spy
  private final BetMapper betMapper = new BetMapperImpl();

  @Captor
  private ArgumentCaptor<Bet> betCaptor;

  @ParameterizedTest
  @MethodSource("provideBetsAndUserId")
  public void shouldFindAllBetsByUserIdAndBetsShouldBeVisible(List<Bet> bets, Long otherUserId) {
    //given
    Long myUserId = 1L;

    List<BetDTO> expectedBetDTOs = bets.stream()
        .map(betMapper::toBetDTO)
        .collect(Collectors.toList());

    when(betRepository.findByUserId(eq(otherUserId), any(Pageable.class))).thenReturn(bets);

    //when
    List<BetDTO> actualBetDTOs = betService.findAllByUserId(0, otherUserId, myUserId);

    //then
    assertThat(actualBetDTOs).hasSize(expectedBetDTOs.size());
    assertThat(actualBetDTOs).containsExactlyElementsOf(expectedBetDTOs);
  }

  @Test
  public void shouldReturnEmptyBetsListBecauseStartMatchRoundIsEarlierAndTheyAreNotOurBets() {
    //given
    Long myUserId = 1L;
    Long otherUserId = 2L;

    List<Bet> bets = Stream.generate(() -> createNotVisibleBet(RandomUtils.nextLong(0L, 100L)))
        .limit(10)
        .peek(bet -> bet.getUser().setId(otherUserId))
        .collect(Collectors.toList());

    when(betRepository.findByUserId(eq(otherUserId), any(Pageable.class))).thenReturn(bets);

    //when
    List<BetDTO> actualBetDTOs = betService.findAllByUserId(0, otherUserId, myUserId);

    //then
    assertThat(actualBetDTOs).isEmpty();
  }

  @Test
  public void shouldFindBetById() throws BetNotFoundException {
    //given
    Long betId = 1L;
    Long userId = 1L;

    Bet bet = createBetForUser(betId, userId);
    BetDTO expectedBetDTO = betMapper.toBetDTO(bet);

    when(betRepository.findById(betId)).thenReturn(Optional.of(bet));

    //when
    BetDTO actualBetDTO = betService.findById(betId, userId);

    //then
    assertThat(actualBetDTO).isEqualTo(expectedBetDTO);
  }

  @Test
  public void shouldFindBetByIdThrowsExceptionBecauseBetIsNotVisible() {
    //given
    Long betId = 1L;
    Long userId = 1L;
    Long otherUserId = 2L;

    Bet bet = createNotVisibleBet(betId);
    bet.getUser().setId(otherUserId);

    when(betRepository.findById(betId)).thenReturn(Optional.of(bet));

    String errorMessage = String.format("Bet doesnt exist or it is not visible for you. Bet id: %d", betId);

    //whenThen
    assertThatThrownBy(() -> betService.findById(betId, userId))
        .isInstanceOf(BetNotFoundException.class)
        .hasMessage(errorMessage);
  }

  @Test
  public void shouldFindBetByIdThrowsExceptionBecauseWrongBetId() {
    //given
    Long betId = 1L;
    Long userId = 1L;

    when(betRepository.findById(betId)).thenReturn(Optional.empty());

    String errorMessage = String.format("Bet doesnt exist or it is not visible for you. Bet id: %d", betId);

    //whenThen
    assertThatThrownBy(() -> betService.findById(betId, userId))
        .isInstanceOf(BetNotFoundException.class)
        .hasMessage(errorMessage);
  }

  @ParameterizedTest
  @MethodSource("provideBetsAndUserId")
  public void shouldFindAllVisibleBets(List<Bet> bets, Long otherUserId) {
    //given
    Long myUserId = 1L;

    List<BetDTO> expectedBetDTOs = bets.stream()
        .map(betMapper::toBetDTO)
        .collect(Collectors.toList());

    when(betRepository.findAll()).thenReturn(bets);

    //when
    List<BetDTO> actualBetDTOs = betService.findAllThatShouldBeVisible(0, myUserId);

    //then
    assertThat(actualBetDTOs).hasSize(expectedBetDTOs.size());
    assertThat(actualBetDTOs).containsExactlyElementsOf(expectedBetDTOs);
  }

  @Test
  public void shouldFindAllVisibleBetsReturnEmptyListBecauseBetsAreNotVisible() {
    //given
    Long userId = 1L;
    Long otherUserId = 2L;

    List<Bet> bets = Stream.generate(() -> createNotVisibleBet(RandomUtils.nextLong(0L, 100L)))
        .limit(10)
        .peek(bet -> bet.getUser().setId(otherUserId))
        .collect(Collectors.toList());

    when(betRepository.findAll()).thenReturn(bets);

    //when
    List<BetDTO> actualBetDTOs = betService.findAllThatShouldBeVisible(0, userId);

    //then
    assertThat(actualBetDTOs).isEmpty();
  }

  @Test
  public void shouldSaveBetThrowErrorBecauseWrongMatchId() {
    //given
    BetDTO betDTO = TestDataUtils.generateBetDTO(1L);
    Long matchId = 1L;

    when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

    String expectedErrorMessage = String.format("Cannot find match with id: %d", matchId);

    //whenThen
    assertThatThrownBy(() -> betService.save(1L, matchId, betDTO))
        .isInstanceOf(MatchNotFoundException.class)
        .hasMessage(expectedErrorMessage);
  }

  @Test
  public void shouldSaveBetThrowErrorBecauseMatchRoundTimePassed() {
    //given
    BetDTO betDTO = TestDataUtils.generateBetDTO(1L);

    Long matchId = 1L;
    Match matchWithEarlyStartRoundTime = TestDataUtils.generateMatch(matchId);
    matchWithEarlyStartRoundTime.getMatchRound().setStartRoundTime(LocalDateTime.MIN);

    when(matchRepository.findById(matchId)).thenReturn(Optional.of(matchWithEarlyStartRoundTime));

    String expectedErrorMessage = "You can only bet until match round is not started";

    //whenThen
    assertThatThrownBy(() -> betService.save(1L, matchId, betDTO))
        .isInstanceOf(BettingRulesException.class)
        .hasMessage(expectedErrorMessage);
  }

  @Test
  public void shouldSaveBetThrowErrorBecauseManyBetsForOneMatchByOneUser() {
    //given
    Long matchId = 1L;
    mockSaveBetWithCorrectMatch(matchId);

    List<Bet> bets = Stream.generate(() -> TestDataUtils.generateBet(RandomUtils.nextLong(0L, 10L)))
        .limit(3)
        .collect(Collectors.toList());

    Long userId = 1L;
    when(betRepository.findByUserIdAndMatchId(userId, matchId)).thenReturn(bets);

    BetDTO betDTO = TestDataUtils.generateBetDTO(1L);
    String expectedErrorMessage = String.format("User id: %d has more than one bet for the match id: %d", userId, matchId);

    //whenThen
    assertThatThrownBy(() -> betService.save(userId, matchId, betDTO))
        .isInstanceOf(BettingRulesException.class)
        .hasMessage(expectedErrorMessage);
  }

  @Test
  public void shouldSaveBetThrowErrorBecauseWrongUserId() {
    //given
    Long matchId = 1L;
    mockSaveBetWithCorrectMatch(matchId);

    List<Bet> bets = Collections.singletonList(TestDataUtils.generateBet(1L));
    Long userId = 1L;
    when(betRepository.findByUserIdAndMatchId(userId, matchId)).thenReturn(bets);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    BetDTO betDTO = TestDataUtils.generateBetDTO(1L);
    String expectedErrorMessage = String.format("Cannot find user with id: %d", userId);

    //whenThen
    assertThatThrownBy(() -> betService.save(userId, matchId, betDTO))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage(expectedErrorMessage);
  }

  @Test
  public void shouldSaveNewBet()
      throws UserNotFoundException, BettingRulesException, MatchNotFoundException {
    //given
    Long userId = 1L;
    Long matchId = 1L;
    List<Bet> emptyBetList = Collections.emptyList();
    mockSaveBet(userId, matchId, emptyBetList);

    BetDTO betDTO = BetDTO.builder()
        .matchId(matchId)
        .awayTeamGoalBet(10L)
        .homeTeamGoalBet(10L)
        .userId(userId)
        .build();

    //when
    betService.save(userId, matchId, betDTO);

    //then
    verify(betRepository, Mockito.times(1)).save(betCaptor.capture());

    Bet actualSaveBet = betCaptor.getValue();
    BetDTO actualSaveBetDTO = betMapper.toBetDTO(actualSaveBet);
    assertThat(actualSaveBetDTO).usingRecursiveComparison()
        .ignoringFields("points")
        .isEqualTo(betDTO);
  }

  @Test
  public void shouldSaveBetByReplacingOldOne()
      throws UserNotFoundException, BettingRulesException, MatchNotFoundException {
    //given
    Long userId = 1L;
    Long matchId = 1L;
    Long betId = 1L;
    List<Bet> betList = Collections.singletonList(TestDataUtils.generateBet(betId));
    mockSaveBet(userId, matchId, betList);

    BetDTO betDTO = BetDTO.builder()
        .id(betId)
        .matchId(matchId)
        .awayTeamGoalBet(10L)
        .homeTeamGoalBet(10L)
        .userId(userId)
        .build();

    //when
    betService.save(userId, matchId, betDTO);

    //then
    verify(betRepository, Mockito.times(1)).save(betCaptor.capture());

    Bet actualSaveBet = betCaptor.getValue();
    BetDTO actualSaveBetDTO = betMapper.toBetDTO(actualSaveBet);
    assertThat(actualSaveBetDTO).usingRecursiveComparison()
        .ignoringFields("points")
        .isEqualTo(betDTO);
  }

  @Test
  public void shouldDeleteBetThrowErrorBecauseWrongUserId() {
    //given
    Long betId = 1L;
    Long userId = 1L;

    Bet betWithDifferentUserId = TestDataUtils.generateBet(betId);
    betWithDifferentUserId.getUser().setId(2L);
    when(betRepository.findById(betId)).thenReturn(Optional.of(betWithDifferentUserId));

    //whenThen
    assertBetDeleteThrowsError(betId, userId);
  }

  @Test
  public void shouldDeleteBetThrowErrorBecauseItContainsResult() {
    //given
    Long betId = 1L;
    Long userId = 1L;

    Bet betWithResult = TestDataUtils.generateBet(betId);
    when(betRepository.findById(betId)).thenReturn(Optional.of(betWithResult));

    //whenThen
    assertBetDeleteThrowsError(betId, userId);
  }

  @Test
  public void shouldDeleteBetThrowErrorBecauseBetDoesntExists() {
    //given
    Long betId = 1L;
    Long userId = 1L;

    when(betRepository.findById(betId)).thenReturn(Optional.empty());

    //whenThen
    assertBetDeleteThrowsError(betId, userId);
  }

  @Test
  public void shouldDeleteBet() throws UnauthorizedUserException {
    //given
    Long betId = 1L;
    Long userId = 1L;

    Bet betWithSameUserIdAndWithoutResult = TestDataUtils.generateBet(betId);
    betWithSameUserIdAndWithoutResult.setBetResults(null);
    betWithSameUserIdAndWithoutResult.getUser().setId(userId);
    when(betRepository.findById(betId)).thenReturn(Optional.of(betWithSameUserIdAndWithoutResult));

    //when
    betService.delete(betId, userId);

    //then
    verify(betRepository, Mockito.times(1)).delete(betWithSameUserIdAndWithoutResult);
  }

  private void assertBetDeleteThrowsError(Long betId, Long userId) {
    assertThatThrownBy(() -> betService.delete(betId, userId))
        .isInstanceOf(UnauthorizedUserException.class)
        .hasMessage("You are not the owner of the bet or bet contains result");
  }

  private void mockSaveBetWithCorrectMatch(Long matchId) {
    Match matchWithRoundTimeNotPassed = TestDataUtils.generateMatch(matchId);
    matchWithRoundTimeNotPassed.getMatchRound().setStartRoundTime(LocalDateTime.MAX);
    when(matchRepository.findById(matchId)).thenReturn(Optional.of(matchWithRoundTimeNotPassed));
  }

  private void mockSaveBet(Long userId, Long matchId, List<Bet> bets) {
    mockSaveBetWithCorrectMatch(matchId);
    when(betRepository.findByUserIdAndMatchId(userId, matchId)).thenReturn(bets);
    when(userRepository.findById(userId)).thenReturn(Optional.of(TestDataUtils.generateUser(userId)));
  }

  private static Stream<Arguments> provideBetsAndUserId() {
    return Stream.of(
        Arguments.of(
            Stream.generate(() -> createVisibleBet(RandomUtils.nextLong(0L, 100L)))
                .limit(10)
                .peek(bet -> bet.getUser().setId(2L))
                .collect(Collectors.toList()),
            2L
        ), //Bets start match round time after now, we can check other users bets (userId = 2)
        Arguments.of(
            Stream.generate(() -> createBetForUser(RandomUtils.nextLong(0L, 100L), 1L))
                .limit(10)
                .collect(Collectors.toList()),
            1L
        ) //Bets start match round time is not relevant, because we check our own bets (userId = 1)
    );
  }

  /**
   * Bet is visible when now is after start match round time
   */
  private static Bet createVisibleBet(Long betId) {
    Bet bet = TestDataUtils.generateBet(betId);

    MatchRound matchRound = bet.getMatch().getMatchRound();
    matchRound.setStartRoundTime(LocalDateTime.MIN);

    return bet;
  }

  /**
   * Bet is not visible when start match round time is before now
   */
  private static Bet createNotVisibleBet(Long betId) {
    Bet bet = TestDataUtils.generateBet(betId);

    MatchRound matchRound = bet.getMatch().getMatchRound();
    matchRound.setStartRoundTime(LocalDateTime.MAX);

    return bet;
  }

  /**
   * Bet is visible for given user that created this bet
   */
  private static Bet createBetForUser(Long betId, Long userId) {
    Bet bet = TestDataUtils.generateBet(betId);

    User user = bet.getUser();
    user.setId(userId);

    return bet;
  }
}
