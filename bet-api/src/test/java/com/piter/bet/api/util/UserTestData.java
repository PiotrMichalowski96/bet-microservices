package com.piter.bet.api.util;

import com.piter.api.commons.domain.User;
import com.piter.bet.api.model.UserResultProjection;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UserTestData {

  public static User createFirstUser() {
    return new User("Jon", "Snow", "snowboard");
  }

  public static UserResultProjection createFirstUserResult() {
    return new UserResultProjection(createFirstUser(), 1L);
  }

  public static User createSecondUser() {
    return new User("Tyrion", "Lanister", "bigGuy");
  }

  public static User createThirdUser() {
    return new User("Robb", "Stark", "bridegroom");
  }

  public static User createFourthUser() {
    return new User("Arya", "Stark", "needle");
  }

  public static List<UserResultProjection> createUserResultList() {
    return List.of(
        createFirstUserResult(),
        new UserResultProjection(createSecondUser(), 2L),
        new UserResultProjection(createThirdUser(), 10L)
    );
  }
}
