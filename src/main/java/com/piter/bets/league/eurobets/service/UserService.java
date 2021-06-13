package com.piter.bets.league.eurobets.service;

import com.piter.bets.league.eurobets.entity.User;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import java.util.List;

public interface UserService {

  List<User> findAll();

  User findById(Long id) throws UserNotFoundException;
}
