package com.piter.match.api.it.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.piter.api.commons.domain.Match;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;

class JsonUtilTest {

  @Test
  void shouldConvertJson() throws IOException {
    //given
    var jsonPath = "samples/retrieve_scenario/get_match_by_id_1.json";

    //when
    Match match = JsonUtil.readJsonFile(jsonPath, Match.class);

    //then
    assertThat(match).isNotNull();
  }

  @Test
  void shouldConvertJsonArrayWithManyElements() throws IOException {
    //given
    var jsonArrayPath = "samples/retrieve_scenario/get_all_matches.json";

    //when
    List<Match> matches = JsonUtil.readJsonArrayFile(jsonArrayPath, Match.class);

    //then
    assertThat(matches).hasSize(3);
    matches.forEach(stock -> assertThat(stock).isNotNull());
  }
}