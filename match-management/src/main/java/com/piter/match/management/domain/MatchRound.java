package com.piter.match.management.domain;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.jackson.Jacksonized;
import org.springframework.format.annotation.DateTimeFormat;

@Jacksonized
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MatchRound {
  private String roundName;
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
  private LocalDateTime startTime;
}
