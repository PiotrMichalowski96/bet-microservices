package com.piter.bets.league.eurobets.repository;

import com.piter.bets.league.eurobets.entity.Match;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

  List<Match> findAllByOrderByMatchStartTimeDesc(Pageable pageable);

  @Query("SELECT m FROM Match m INNER JOIN m.matchRound mr ORDER BY mr.startRoundTime DESC")
  List<Match> findAllByOrderByMatchRoundStartTimeDesc(Pageable pageable);
}
