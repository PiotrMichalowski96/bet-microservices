package com.piter.bets.league.eurobets.controller;

import com.piter.bets.league.eurobets.dto.UserDTO;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import com.piter.bets.league.eurobets.security.UserPrincipal;
import com.piter.bets.league.eurobets.service.UserService;
import java.nio.file.attribute.UserPrincipalNotFoundException;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @GetMapping("/{id}")
  public UserDTO findById(@PathVariable Long id) throws UserNotFoundException {
    UserDTO user = userService.findById(id);
    logger.info("Find by id " + user.toString());
    return user;
  }

  @GetMapping
  public List<UserDTO> findAll() {
    return userService.findAll();
  }

  @GetMapping("/currentUser")
  public UserDTO findCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal)
      throws UserPrincipalNotFoundException {
    if (Objects.nonNull(userPrincipal)) {
      return userService.getUserDetails(userPrincipal.getUser());
    } else {
      throw new UserPrincipalNotFoundException("You are not logged in");
    }
  }
}
