package com.piter.bets.league.eurobets.service;

import static com.piter.bets.league.eurobets.util.TestDataUtils.generateMatchDTO;
import static com.piter.bets.league.eurobets.util.TestDataUtils.generateMatchRound;
import static org.assertj.core.api.Assertions.assertThat;
import static com.piter.bets.league.eurobets.util.TestDataUtils.generateMatch;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.piter.bets.league.eurobets.dto.MatchDTO;
import com.piter.bets.league.eurobets.entity.Bet;
import com.piter.bets.league.eurobets.entity.Match;
import com.piter.bets.league.eurobets.entity.MatchRound;
import com.piter.bets.league.eurobets.entity.User;
import com.piter.bets.league.eurobets.exception.MatchNotFoundException;
import com.piter.bets.league.eurobets.exception.MatchRoundNotFoundException;
import com.piter.bets.league.eurobets.mapper.MatchMapper;
import com.piter.bets.league.eurobets.mapper.MatchMapperImpl;
import com.piter.bets.league.eurobets.repository.BetRepository;
import com.piter.bets.league.eurobets.repository.BetResultsRepository;
import com.piter.bets.league.eurobets.repository.MatchRepository;
import com.piter.bets.league.eurobets.repository.MatchRoundRepository;
import com.piter.bets.league.eurobets.repository.UserRepository;
import com.piter.bets.league.eurobets.repository.projection.UserPoints;
import com.piter.bets.league.eurobets.util.TestDataUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;

@ExtendWith(MockitoExtension.class)
public class MatchServiceTest {

  @InjectMocks
  private MatchServiceImpl matchService;

  @Mock
  private MatchRepository matchRepository;

  @Mock
  private MatchRoundRepository matchRoundRepository;

  @Mock
  private BetRepository betRepository;

  @Captor
  private ArgumentCaptor<List<Bet>> betsCaptor;

  @Mock
  private BetResultsRepository betResultsRepository;

  @Mock
  private UserRepository userRepository;

  @Captor
  private ArgumentCaptor<List<User>> usersCaptor;

  @Spy
  private final MatchMapper matchMapper = new MatchMapperImpl();

  @Test
  public void shouldFindMatchesByStartTime() {
    //given
    List<Match> matches = Stream.generate(() -> generateMatch(RandomUtils.nextLong(0, 100)))
        .limit(10)
        .collect(Collectors.toList());

    when(matchRepository.findAllByOrderByMatchStartTimeDesc(any(Pageable.class))).thenReturn(matches);

    List<MatchDTO> expectedMatches = matches.stream()
        .map(matchMapper::toMatchDTO)
        .collect(Collectors.toList());

    //when
    List<MatchDTO> matchDTOs = matchService.findAllByMatchStartTime(0);

    //then
    assertThat(matchDTOs).hasSize(expectedMatches.size());
    assertThat(matchDTOs).containsExactlyElementsOf(expectedMatches);
  }

  @Test
  public void shouldFindMatchesByMatchRound() {
    //given
    List<Match> matches = Stream.generate(() -> generateMatch(RandomUtils.nextLong(0, 100)))
        .limit(10)
        .collect(Collectors.toList());

    when(matchRepository.findAllByOrderByMatchRoundStartTimeDesc(any(Pageable.class))).thenReturn(matches);

    List<MatchDTO> expectedMatches = matches.stream()
        .map(matchMapper::toMatchDTO)
        .collect(Collectors.toList());

    //when
    List<MatchDTO> matchDTOs = matchService.findAllByMatchRound(0);

    //then
    assertThat(matchDTOs).hasSize(expectedMatches.size());
    assertThat(matchDTOs).containsExactlyElementsOf(expectedMatches);
  }

  @Test
  public void shouldFindAllMatches() {
    //given
    List<Match> matches = Stream.generate(() -> generateMatch(RandomUtils.nextLong(0, 100)))
        .limit(10)
        .collect(Collectors.toList());

    Page<Match> matchPage = new PageImpl<>(matches);

    when(matchRepository.findAll(any(Pageable.class))).thenReturn(matchPage);

    List<MatchDTO> expectedMatches = matches.stream()
        .map(matchMapper::toMatchDTO)
        .collect(Collectors.toList());

    //when
    List<MatchDTO> matchDTOs = matchService.findAll(0);

    //then
    assertThat(matchDTOs).hasSize(expectedMatches.size());
    assertThat(matchDTOs).containsExactlyElementsOf(expectedMatches);
  }

  @Test
  public void shouldFindMatchWithGivenId() throws MatchNotFoundException {
    //given
    final Long matchId = 1L;
    Match match = generateMatch(matchId);

    when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

    MatchDTO expectedMatchDTO = matchMapper.toMatchDTO(match);

    //when
    MatchDTO actualMatchDTO = matchService.findById(matchId);

    //then
    assertThat(actualMatchDTO).isEqualTo(expectedMatchDTO);
  }

  @Test
  public void shouldThrowMatchNotFoundExceptionDueToWrongId() {
    //given
    final Long matchId = 99999L;
    final String errorMessage = String.format("Cannot find match with id: %d", matchId);

    when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

    //whenThen
    assertThatThrownBy(() -> matchService.findById(matchId))
        .isInstanceOf(MatchNotFoundException.class)
        .hasMessage(errorMessage);
  }

  @Test
  public void shouldSaveMatchWithoutMatchResult() throws MatchRoundNotFoundException {
    //given
    final Long matchId = 1L;
    MatchDTO matchDTO = createMatchDtoWithoutMatchResult(matchId);

    Long matchRoundId = matchDTO.getMatchRoundId();
    MatchRound matchRound = generateMatchRound(matchRoundId);

    when(matchRoundRepository.findById(matchRoundId)).thenReturn(Optional.of(matchRound));

    Match matchSaved = createSavedMatch(matchDTO, matchRound);
    when(matchRepository.save(any(Match.class))).thenReturn(matchSaved);

    //when
    MatchDTO actualMatchDTO = matchService.save(matchDTO);

    //then
    verify(matchRepository, Mockito.times(1)).save(any(Match.class));
    assertThat(actualMatchDTO).isEqualTo(matchDTO);
  }

  @Test
  public void shouldThrowMatchRoundNotFoundExceptionDueToWrongMatchRoundId() {
    //given
    final Long matchRoundId = 99999L;
    final String errorMessage = String.format("Cannot find match round with id: %d", matchRoundId);

    MatchDTO matchDTO = generateMatchDTO(1L);
    matchDTO.setMatchRoundId(matchRoundId);

    when(matchRoundRepository.findById(matchRoundId)).thenReturn(Optional.empty());

    //whenThen
    assertThatThrownBy(() -> matchService.save(matchDTO))
        .isInstanceOf(MatchRoundNotFoundException.class)
        .hasMessage(errorMessage);
  }

  @Test
  public void shouldSaveMatchWithMatchResultsInOrderToAddBetsWithResults()
      throws MatchRoundNotFoundException {

    //given
    final Long matchId = 1L;
    MatchDTO matchDTO = generateMatchDTO(matchId);

    commonMockForSaveMatchMethod(matchDTO);

    List<Bet> betsWithoutResults = Stream
        .generate(() -> TestDataUtils.generateBet(RandomUtils.nextLong(0, 100)))
        .limit(10)
        .peek(bet -> bet.setBetResults(null))
        .collect(Collectors.toList());

    when(betRepository.findByMatchId(matchId)).thenReturn(betsWithoutResults);

    //when
    matchService.save(matchDTO);

    //then
    verify(betRepository, Mockito.times(1)).saveAll(betsCaptor.capture());

    List<Bet> betsWithResults = betsCaptor.getValue();

    assertThat(betsWithResults).hasSize(10);
    betsWithResults.forEach(
        bet -> assertThat(bet.getBetResults()).isNotNull()
    );
  }

  @Test
  public void shouldSaveMatchWithMatchResultsInOrderToCalculatePointsForUsers()
      throws MatchRoundNotFoundException {

    //given
    final Long matchId = 1L;
    MatchDTO matchDTO = generateMatchDTO(matchId);

    commonMockForSaveMatchMethod(matchDTO);

    List<User> usersWithZeroPoints = Stream.generate(() -> TestDataUtils.generateUser(RandomUtils.nextLong(0, 100)))
        .limit(3)
        .peek(user -> user.setPoints(0L))
        .collect(Collectors.toList());

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    List<UserPoints> userPointsList = List.of(
        createUserPointsProjection(factory, usersWithZeroPoints.get(0), 5L),
        createUserPointsProjection(factory, usersWithZeroPoints.get(1), 10L),
        createUserPointsProjection(factory, usersWithZeroPoints.get(2), 20L)
    );

    when(betResultsRepository.sumTotalPointsForUsers()).thenReturn(userPointsList);

    //when
    matchService.save(matchDTO);

    //then
    verify(userRepository, Mockito.times(1)).saveAll(usersCaptor.capture());

    List<User> usersWithCalculatedPoints = usersCaptor.getValue();

    assertThat(usersWithCalculatedPoints).hasSize(3);
    assertThat(usersWithCalculatedPoints.get(0).getPoints()).isEqualTo(5L);
    assertThat(usersWithCalculatedPoints.get(1).getPoints()).isEqualTo(10L);
    assertThat(usersWithCalculatedPoints.get(2).getPoints()).isEqualTo(20L);
  }

  @Test
  public void shouldDeleteMatch() throws MatchNotFoundException {
    //given
    final Long matchId = 1L;
    Match match = generateMatch(matchId);

    when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

    //when
    matchService.delete(matchId);

    //then
    verify(matchRepository).delete(match);
    verify(betRepository).deleteByMatchId(matchId);
  }

  @Test
  public void shouldThrowMatchNotFoundExceptionDuringDeleteMatch() {
    //given
    final Long matchId = 99999L;
    String errorMessage = String.format("Cannot find match with id: %d", matchId);

    when(matchRepository.findById(matchId)).thenReturn(Optional.empty());

    //whenThen
    assertThatThrownBy(() -> matchService.delete(matchId))
        .isInstanceOf(MatchNotFoundException.class)
        .hasMessage(errorMessage);
  }

  @Test
  public void shouldCalculatePointsForUsersDuringDeleteMatch() throws MatchNotFoundException {
    //given
    final Long matchId = 1L;
    Match match = generateMatch(matchId);

    when(matchRepository.findById(matchId)).thenReturn(Optional.of(match));

    List<User> users = Stream.generate(() -> TestDataUtils.generateUser(RandomUtils.nextLong(0, 100)))
        .limit(3)
        .collect(Collectors.toList());

    ProjectionFactory factory = new SpelAwareProxyProjectionFactory();
    List<UserPoints> userPointsList = List.of(
        createUserPointsProjection(factory, users.get(0), users.get(0).getPoints()),
        createUserPointsProjection(factory, users.get(1), users.get(1).getPoints()),
        createUserPointsProjection(factory, users.get(2), users.get(2).getPoints())
    );

    when(betResultsRepository.sumTotalPointsForUsers()).thenReturn(userPointsList);

    //when
    matchService.delete(matchId);

    //then
    verify(userRepository, Mockito.times(1)).saveAll(usersCaptor.capture());

    List<User> usersWithCalculatedPoints = usersCaptor.getValue();

    assertThat(usersWithCalculatedPoints).hasSize(3);
    assertThat(usersWithCalculatedPoints.get(0).getPoints()).isEqualTo(users.get(0).getPoints());
    assertThat(usersWithCalculatedPoints.get(1).getPoints()).isEqualTo(users.get(1).getPoints());
    assertThat(usersWithCalculatedPoints.get(2).getPoints()).isEqualTo(users.get(2).getPoints());
  }

  private void commonMockForSaveMatchMethod(MatchDTO matchDTO) {
    Long matchRoundId = matchDTO.getMatchRoundId();
    MatchRound matchRound = generateMatchRound(matchRoundId);
    when(matchRoundRepository.findById(matchRoundId)).thenReturn(Optional.of(matchRound));
  }

  private UserPoints createUserPointsProjection(ProjectionFactory factory, User user, Long points) {
    Map<String, Object> map = Map.of("user", user, "points", points);
    return factory.createProjection(UserPoints.class, map);
  }

  private MatchDTO createMatchDtoWithoutMatchResult(Long matchId) {
    MatchDTO matchDTO = generateMatchDTO(matchId);
    matchDTO.setHomeTeamGoals(null);
    matchDTO.setAwayTeamGoals(null);
    return matchDTO;
  }

  private Match createSavedMatch(MatchDTO matchDTO, MatchRound matchRound) {
    Match savedMatch = matchMapper.toMatch(matchDTO);
    savedMatch.setMatchRound(matchRound);
    return savedMatch;
  }
}
