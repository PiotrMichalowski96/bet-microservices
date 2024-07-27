package com.piter.api.commons.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.event.MatchEvent;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

  @Test
  void shouldConvertJson() throws IOException {
    //given
    var jsonPath = "src/test/resources/samples/single.json";

    //when
    MatchEvent match = JsonUtil.readJsonFile(jsonPath, MatchEvent.class);

    //then
    assertThat(match).isNotNull();
  }

  @Test
  void shouldConvertJsonArrayWithManyElements() throws IOException {
    //given
    var jsonArrayPath = "src/test/resources/samples/array.json";

    //when
    List<MatchEvent> matches = JsonUtil.readJsonArrayFile(jsonArrayPath, MatchEvent.class);

    //then
    assertThat(matches)
        .hasSize(3)
        .allMatch(Objects::nonNull);
  }
}