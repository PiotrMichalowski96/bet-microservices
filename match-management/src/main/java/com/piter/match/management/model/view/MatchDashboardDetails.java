package com.piter.match.management.model.view;

import com.piter.match.management.domain.Match;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MatchDashboardDetails {
  private Integer currentPage;
  private Integer totalPages;
  private List<Match> matches;
  private LocalDateTime loadTime;
}
