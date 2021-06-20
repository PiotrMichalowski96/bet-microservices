package com.piter.bets.league.eurobets.repository.projection;

import com.piter.bets.league.eurobets.entity.User;

public interface UserPoints {
  User getUser();
  Long getPoints();
}
