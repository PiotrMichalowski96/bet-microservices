package com.piter.bets.league.eurobets.controllers;

import com.piter.bets.league.eurobets.service.BetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class BetControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private BetService betService;

  //TODO: implement tests

}
