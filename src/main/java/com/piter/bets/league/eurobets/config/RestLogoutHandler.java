package com.piter.bets.league.eurobets.config;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

public class RestLogoutHandler implements LogoutSuccessHandler {

  @Override
  public void onLogoutSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    response.getWriter().append("OK");
    response.setStatus(200);
  }
}
