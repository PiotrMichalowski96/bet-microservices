package com.piter.bets.league.eurobets.service;

import static com.piter.bets.league.eurobets.util.TestDataUtils.generateUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.piter.bets.league.eurobets.dto.UserDTO;
import com.piter.bets.league.eurobets.entity.User;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import com.piter.bets.league.eurobets.mapper.UserMapper;
import com.piter.bets.league.eurobets.mapper.UserMapperImpl;
import com.piter.bets.league.eurobets.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

  @InjectMocks
  private UserServiceImpl userService;

  @Mock
  private UserRepository userRepository;

  @Spy
  private final UserMapper userMapper = new UserMapperImpl();

  @Test
  public void shouldReturnUserWithGivenId() throws UserNotFoundException {
    //given
    final Long userId = 1L;
    User user = generateUser(userId);

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));

    UserDTO expectedUserDTO = userMapper.toUserDTO(user);

    //when
    UserDTO actualUserDTO = userService.findById(userId);

    //then
    assertThat(actualUserDTO).isEqualTo(expectedUserDTO);
  }

  @Test
  public void shouldThrowUserNotFoundExceptionDueToWrongId() {
    //given
    final Long userId = 99999L;
    final String errorMessage = String.format("Cannot find user with id: %d", userId);

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    //whenThen
    assertThatThrownBy(() -> userService.findById(userId))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage(errorMessage);
  }

  @Test
  public void shouldFindAllUsers() {
    //given
    List<User> users = Stream.generate(() -> generateUser(RandomUtils.nextLong(0, 100)))
        .limit(10)
        .collect(Collectors.toList());

    when(userRepository.findAll(any(Sort.class))).thenReturn(users);

    List<UserDTO> expectedUsers = users.stream()
        .map(userMapper::toUserDTO)
        .collect(Collectors.toList());

    //when
    List<UserDTO> userDTOs = userService.findAll();

    //then
    assertThat(userDTOs).hasSize(expectedUsers.size());
    assertThat(userDTOs).containsExactlyElementsOf(expectedUsers);
  }
}
