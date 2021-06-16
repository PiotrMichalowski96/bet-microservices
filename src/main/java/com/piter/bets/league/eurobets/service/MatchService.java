package com.piter.bets.league.eurobets.service;

import com.piter.bets.league.eurobets.dto.MatchDTO;
import com.piter.bets.league.eurobets.exception.MatchNotFoundException;
import java.util.List;

public interface MatchService {

  List<MatchDTO> findAllByMatchStartTime(Integer pageNumber);

  List<MatchDTO> findAll(Integer pageNumber);

  MatchDTO findById(Long id) throws MatchNotFoundException;

  MatchDTO save(MatchDTO matchDTO);

  void delete(Long id) throws MatchNotFoundException;
}
