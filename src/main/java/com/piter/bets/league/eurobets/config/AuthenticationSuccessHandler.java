package com.piter.bets.league.eurobets.config;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse,
      Authentication authentication) throws IOException {

    httpServletResponse.getWriter().append("OK");
    httpServletResponse.setStatus(200);
  }
}