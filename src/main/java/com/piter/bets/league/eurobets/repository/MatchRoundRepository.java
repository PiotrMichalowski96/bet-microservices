package com.piter.bets.league.eurobets.repository;

import com.piter.bets.league.eurobets.entity.MatchRound;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRoundRepository extends JpaRepository<MatchRound, Long> {

}
