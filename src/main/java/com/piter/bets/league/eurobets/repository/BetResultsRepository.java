package com.piter.bets.league.eurobets.repository;

import com.piter.bets.league.eurobets.entity.BetResults;
import com.piter.bets.league.eurobets.repository.projection.UserPoints;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface BetResultsRepository extends JpaRepository<BetResults, Long> {

  @Query("SELECT bet.user AS user, SUM(br.points) AS points FROM BetResults br INNER JOIN br.bet bet GROUP BY bet.user")
  List<UserPoints> sumTotalPointsForUsers();
}