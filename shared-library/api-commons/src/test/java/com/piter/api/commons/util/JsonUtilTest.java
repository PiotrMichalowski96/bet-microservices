package com.piter.api.commons.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.domain.Match;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

  @Test
  void shouldConvertJson() throws IOException {
    //given
    var jsonPath = "src/test/resources/samples/single.json";

    //when
    Match match = JsonUtil.readJsonFile(jsonPath, Match.class);

    //then
    assertThat(match).isNotNull();
  }

  @Test
  void shouldConvertJsonArrayWithManyElements() throws IOException {
    //given
    var jsonArrayPath = "src/test/resources/samples/array.json";

    //when
    List<Match> matches = JsonUtil.readJsonArrayFile(jsonArrayPath, Match.class);

    //then
    assertThat(matches).hasSize(3);
    matches.forEach(stock -> assertThat(stock).isNotNull());
  }
}