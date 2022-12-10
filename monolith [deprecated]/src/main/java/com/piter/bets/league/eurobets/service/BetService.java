package com.piter.bets.league.eurobets.service;

import com.piter.bets.league.eurobets.dto.BetDTO;
import com.piter.bets.league.eurobets.exception.BetNotFoundException;
import com.piter.bets.league.eurobets.exception.MatchNotFoundException;
import com.piter.bets.league.eurobets.exception.BettingRulesException;
import com.piter.bets.league.eurobets.exception.UnauthorizedUserException;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import java.util.List;

public interface BetService {

  List<BetDTO> findAllByUserId(Integer pageNumber, Long userId, Long myUserId);

  BetDTO findById(Long betId, Long myUserId) throws BetNotFoundException;

  List<BetDTO> findAllThatShouldBeVisible(Integer pageNumber, Long userId);

  BetDTO save(Long userId, Long matchId, BetDTO betDTO)
      throws MatchNotFoundException, BettingRulesException, UserNotFoundException;

  void delete(Long betId, Long userId) throws UnauthorizedUserException;
}
