package com.piter.bets.league.eurobets.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.piter.bets.league.eurobets.dto.UserDTO;
import com.piter.bets.league.eurobets.exception.UserNotFoundException;
import com.piter.bets.league.eurobets.service.UserService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private UserService userService;

  @WithUserDetails(value = "user")
  @Test
  public void shouldReturnUserWithGivenId() throws Exception {
    //given
    UserDTO user = new UserDTO(1L, "exampleUsername", 1);
    when(userService.findById(1L)).thenReturn(user);

    //whenThen
    mockMvc.perform(get("/users/1")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", is(user.getId().intValue())))
        .andExpect(jsonPath("$.username", is(user.getUsername())))
        .andExpect(jsonPath("$.points", is(user.getPoints())));
  }

  @WithUserDetails(value = "user")
  @Test
  public void shouldReturnBadRequestForNotExistingId() throws Exception {
    //given
    UserDTO user = new UserDTO(1L,"exampleUsername",1);
    int notExistingId = 2;

    String message = String.format("Cannot find user with id: %d", notExistingId);
    when(userService.findById(2L)).thenThrow(new UserNotFoundException(message));

    String url = String.format("/users/%d", notExistingId);

    //whenThen
    mockMvc.perform(get(url)
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message", is(message)));
  }

  @WithUserDetails(value = "user")
  @Test
  public void shouldReturnUserList() throws Exception {
    //given
    UserDTO firstUser = new UserDTO(1L, "First User", 10);
    UserDTO secondUser = new UserDTO(2L, "Second User", 100);

    when(userService.findAll()).thenReturn(List.of(firstUser, secondUser));

    //whenThen
    mockMvc.perform(get("/users")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$", hasSize(2)))

        .andExpect(jsonPath("$[0].id", is(firstUser.getId().intValue())))
        .andExpect(jsonPath("$[0].username", is(firstUser.getUsername())))
        .andExpect(jsonPath("$[0].points", is(firstUser.getPoints())))

        .andExpect(jsonPath("$[1].id", is(secondUser.getId().intValue())))
        .andExpect(jsonPath("$[1].username", is(secondUser.getUsername())))
        .andExpect(jsonPath("$[1].points", is(secondUser.getPoints())));

  }
}
