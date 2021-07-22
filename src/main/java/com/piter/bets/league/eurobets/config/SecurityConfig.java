package com.piter.bets.league.eurobets.config;

import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter implements WebMvcConfigurer {

  private final UserDetailsService userDetailsService;
  private final AuthenticationEntryPoint authenticationEntryPoint;

  private final AuthenticationSuccessHandler authSuccessHandler = new AuthenticationSuccessHandler();
  private final SimpleUrlAuthenticationFailureHandler authFailureHandler = new SimpleUrlAuthenticationFailureHandler();
  private final LogoutSuccessHandler logoutSuccessHandler = new RestLogoutHandler();

  @Bean
  private static PasswordEncoder getEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  protected void configure(final HttpSecurity http) throws Exception {
    http.cors(Customizer.withDefaults())
        .formLogin()
          .successHandler(authSuccessHandler)
          .failureHandler(authFailureHandler)
          .permitAll()
        .and()
          .logout()
            .logoutSuccessHandler(logoutSuccessHandler)
            .logoutSuccessUrl("/")
        .and()
          .exceptionHandling()
          .authenticationEntryPoint(authenticationEntryPoint)
        .and()
          .authorizeRequests()
            .antMatchers(HttpMethod.GET, "/swagger-ui.html").permitAll()
            .antMatchers(HttpMethod.GET, "/users").hasAnyAuthority("USER", "ADMIN")
            .antMatchers(HttpMethod.GET, "/users/*").hasAnyAuthority("USER", "ADMIN")
            .antMatchers(HttpMethod.GET, "/matches").hasAnyAuthority("USER", "ADMIN")
            .antMatchers(HttpMethod.GET, "/matches/*").hasAnyAuthority("USER", "ADMIN")
            .antMatchers(HttpMethod.POST, "/matches").hasAnyAuthority("ADMIN")
            .antMatchers(HttpMethod.DELETE, "/matches/*").hasAnyAuthority("ADMIN")
            .antMatchers(HttpMethod.GET, "/bets").hasAnyAuthority("USER", "ADMIN")
            .antMatchers(HttpMethod.GET, "/bets/*").hasAnyAuthority("USER", "ADMIN")
            .antMatchers(HttpMethod.GET, "/bets/user/*").hasAnyAuthority("USER", "ADMIN")
            .antMatchers(HttpMethod.POST, "/bets/match/*").hasAnyAuthority("USER", "ADMIN")
            .antMatchers(HttpMethod.DELETE, "/bets/*").hasAnyAuthority("USER", "ADMIN")
        .and()
          .csrf()
          .ignoringAntMatchers("/login", "/logout")
          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
  }

  @Override
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.userDetailsService(userDetailsService)
        .passwordEncoder(getEncoder());
  }

  @Bean
  @ConditionalOnProperty(value = "frontend.app.enabled", havingValue = "true")
  CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:4200"));
    configuration.setAllowCredentials(true);
    configuration.setAllowedHeaders(
        Arrays.asList("Access-Control-Allow-Headers", "Access-Control-Allow-Origin",
            "Access-Control-Request-Method", "Access-Control-Request-Headers", "Origin",
            "Cache-Control",
            "Content-Type", "Authorization", "X-XSRF-TOKEN"));
    configuration.setAllowedMethods(Arrays.asList("DELETE", "GET", "POST", "PATCH", "PUT"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
