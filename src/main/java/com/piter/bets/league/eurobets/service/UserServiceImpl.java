package com.piter.bets.league.eurobets.service;

import com.piter.bets.league.eurobets.entity.User;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import com.piter.bets.league.eurobets.repository.UserRepository;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;

  public UserServiceImpl(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public List<User> findAll() {
    Sort sort = Sort.by("username");
    return userRepository.findAll(sort);
  }

  @Override
  public User findById(Long id) throws UserNotFoundException {
    return userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Cannot find user with id: " + id));
  }
}
