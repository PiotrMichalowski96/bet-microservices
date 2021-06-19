package com.piter.bets.league.eurobets.repository;

import com.piter.bets.league.eurobets.entity.Bet;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BetRepository extends JpaRepository<Bet, Long> {

  List<Bet> findByUserId(Long userId, Pageable pageable);

  List<Bet> findByUserIdAndMatchId(Long userId, Long matchId);
}
