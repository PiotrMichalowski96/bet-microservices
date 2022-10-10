package com.piter.bet.api.util;

import com.piter.bet.api.domain.User;
import java.util.Map;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;

@UtilityClass
public class TokenUtil {

  private static final String NAME_ATTRIBUTE = "name";
  private static final String USERNAME_ATTRIBUTE = "username";

  public static User getUserFrom(BearerTokenAuthentication token) {
    Map<String, ?> attributes = token.getTokenAttributes();
    String fullName = (String) attributes.get(NAME_ATTRIBUTE);
    String username = (String) attributes.get(USERNAME_ATTRIBUTE);
    return createUserFrom(fullName, username);
  }

  private static User createUserFrom(String fullName, String nickname) {
    if (StringUtils.isBlank(fullName) || StringUtils.isBlank(nickname)) {
      throw new IllegalArgumentException("Blank values in argument");
    }
    String[] names = StringUtils.split(fullName, StringUtils.SPACE);
    if (names == null || names.length != 2) {
      throw new IllegalArgumentException("Wrong value full name: " + fullName);
    }
    String firstName = names[0];
    String lastName = names[1];
    return new User(firstName, lastName, nickname);
  }
}
