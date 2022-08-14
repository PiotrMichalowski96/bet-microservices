package com.piter.bets.league.eurobets.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.bets.league.eurobets.dto.UserDTO;
import com.piter.bets.league.eurobets.entity.User;
import com.piter.bets.league.eurobets.util.TestDataUtils;
import org.junit.jupiter.api.Test;

public class UserMapperTest {

  private final UserMapper userMapper = new UserMapperImpl();

  @Test
  public void shouldCorrectMapToUserDTO() {
    //given
    User user = TestDataUtils.generateUser(1L);

    //when
    UserDTO userDTO = userMapper.toUserDTO(user);

    //then
    assertThat(userDTO.getId()).isEqualTo(user.getId());
    assertThat(userDTO.getUsername()).isEqualTo(user.getUsername());
    assertThat(userDTO.getPoints()).isEqualTo(user.getPoints().intValue());
  }
}
