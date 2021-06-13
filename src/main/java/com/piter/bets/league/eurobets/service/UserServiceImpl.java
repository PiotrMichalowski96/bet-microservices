package com.piter.bets.league.eurobets.service;

import com.piter.bets.league.eurobets.dto.UserDTO;
import com.piter.bets.league.eurobets.entity.User;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import com.piter.bets.league.eurobets.mapper.UserMapper;
import com.piter.bets.league.eurobets.repository.UserRepository;
import java.util.List;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public List<UserDTO> findAll() {
    Sort sort = Sort.by(Sort.Direction.DESC, "points");
    List<User> users = userRepository.findAll(sort);

    return users.stream()
            .map(userMapper::toUserDTO)
            .collect(Collectors.toList());
  }

  @Override
  public UserDTO findById(Long id) throws UserNotFoundException {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException("Cannot find user with id: " + id));

    return userMapper.toUserDTO(user);
  }
}
