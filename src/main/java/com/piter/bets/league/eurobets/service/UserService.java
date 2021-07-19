package com.piter.bets.league.eurobets.service;

import com.piter.bets.league.eurobets.dto.UserDTO;
import com.piter.bets.league.eurobets.entity.User;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import java.util.List;

public interface UserService {

  List<UserDTO> findAll();

  UserDTO findById(Long id) throws UserNotFoundException;

  UserDTO getUserDetails(User user);
}
