package com.piter.match.api.web;

import com.piter.api.commons.model.Match;
import com.piter.match.api.service.MatchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v2")
class MatchController {

  private static final String MATCH_TIME_PARAM_VALUE = "match-time";
  private static final String ROUND_TIME_PARAM_VALUE = "round-time";
  private static final String ASC_ORDER_UPCOMING_MATCHES = "asc";
  private static final String DESC_ORDER_UPCOMING_MATCHES = "desc";

  private final MatchService matchService;
  private final Validator validator;
  private final Map<String, Supplier<Flux<Match>>> findAllMapSuppliers;
  private final Map<String, Supplier<Flux<Match>>> findUpcomingMapSuppliers;

  MatchController(MatchService matchService, Validator validator) {
    this.matchService = matchService;
    this.validator = validator;
    this.findAllMapSuppliers = Map.of(
        MATCH_TIME_PARAM_VALUE, matchService::findAllByOrderByMatchStartTime,
        ROUND_TIME_PARAM_VALUE, matchService::findAllByOrderByMatchRoundStartTime
    );
    this.findUpcomingMapSuppliers = Map.of(
        ASC_ORDER_UPCOMING_MATCHES, matchService::findAllUpcomingOrderByStartTimeAsc,
        DESC_ORDER_UPCOMING_MATCHES, matchService::findAllUpcomingOrderByStartTimeDesc
    );
  }

  @Operation(summary = "Find all matches ordered")
  @ApiResponse(responseCode = "200", description = "successful found match list", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Match.class))))
  @GetMapping("/matches")
  Flux<MatchResponse> findAll(
      @Parameter(in = ParameterIn.QUERY, name = "order", example = "match-time, round-time")
      @RequestParam Optional<String> order) {

    return order.map(findAllMapSuppliers::get)
        .map(Supplier::get)
        .orElse(matchService.findAll())
        .map(MatchResponse::of);
  }

  @Operation(summary = "Find all not started matches")
  @ApiResponse(responseCode = "200", description = "successful found upcoming match list", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Match.class))))
  @GetMapping("/matches/upcoming")
  Flux<MatchResponse> findUpcoming(
      @Parameter(in = ParameterIn.QUERY, name = "startTime", example = "desc, asc")
      @RequestParam Optional<String> startTime) {

    return startTime.map(findUpcomingMapSuppliers::get)
        .map(Supplier::get)
        .orElse(matchService.findAllUpcomingOrderByStartTimeAsc())
        .map(MatchResponse::of);
  }

  @Operation(summary = "Find all matches that have been started but not finished yet")
  @ApiResponse(responseCode = "200", description = "successful found ongoing match list", content = @Content(array = @ArraySchema(schema = @Schema(implementation = Match.class))))
  @GetMapping("/matches/ongoing")
  Flux<MatchResponse> findOngoing() {
    return matchService.findAllOngoing()
        .map(MatchResponse::of);
  }

  @Operation(summary = "Find match by id")
  @ApiResponse(responseCode = "200", description = "successful found match by id", content = @Content(schema = @Schema(implementation = Match.class)))
  @ApiResponse(responseCode = "404", description = "match not found")
  @GetMapping("/matches/{id}")
  Mono<MatchResponse> findById(
      @Parameter(in = ParameterIn.PATH, name = "id", example = "1")
      @PathVariable Long id) {

    return matchService.findById(id)
        .map(MatchResponse::of);
  }

  @Operation(summary = "Create new match")
  @ApiResponse(responseCode = "200", description = "ID of match to create", content = @Content(schema = @Schema(implementation = Long.class)))
  @PostMapping("/matches")
  Mono<MatchIdResponse> save(@RequestBody MatchRequest matchRequest) {
    return Mono.just(matchRequest)
        .doOnNext(m -> validate("match", m))
        .map(MatchRequest::toMatch)
        .flatMap(matchService::saveMatch)
        .map(Match::id)
        .map(MatchIdResponse::new);
  }

  @Operation(summary = "Update match")
  @ApiResponse(responseCode = "200", description = "ID of match to updated", content = @Content(schema = @Schema(implementation = Long.class)))
  @PutMapping("/matches/{id}")
  Mono<MatchIdResponse> update(
      @Parameter(in = ParameterIn.PATH, name = "id", example = "1")
      @PathVariable Long id,
      @RequestBody MatchRequest matchRequest) {
    return Mono.just(matchRequest)
        .doOnNext(m -> validate("match", m))
        .map(MatchRequest::toMatch)
        .flatMap(m -> matchService.updateMatch(id, m))
        .map(Match::id)
        .map(MatchIdResponse::new);
  }

  @Operation(summary = "Update match result")
  @ApiResponse(responseCode = "200", description = "ID of match to update result", content = @Content(schema = @Schema(implementation = Long.class)))
  @PatchMapping("/matches/{id}")
  Mono<MatchIdResponse> updateResult(
      @Parameter(in = ParameterIn.PATH, name = "id", example = "1")
      @PathVariable Long id,
      @RequestBody MatchResult matchResult) {
    return Mono.just(matchResult)
        .doOnNext(result -> validate("matchResult", result))
        .map(MatchResult::toMatchResult)
        .flatMap(result -> matchService.updateMatchResult(id, result))
        .map(Match::id)
        .map(MatchIdResponse::new);
  }

  @Operation(summary = "Delete match")
  @ApiResponse(responseCode = "200", description = "successful deleted match by id", content = @Content(schema = @Schema(implementation = Match.class)))
  @ApiResponse(responseCode = "404", description = "match not found")
  @DeleteMapping("/matches/{id}")
  Mono<Void> delete(
      @Parameter(in = ParameterIn.PATH, name = "id", example = "1")
      @PathVariable Long id) {

    return matchService.deleteMatch(id);
  }

  private <T> void validate(String name, T objectToValidate) {
    Errors errors = new BeanPropertyBindingResult(objectToValidate, name);
    validator.validate(objectToValidate, errors);

    if (errors.hasErrors()) {
      throw new ServerWebInputException(errors.toString());
    }
  }
}
