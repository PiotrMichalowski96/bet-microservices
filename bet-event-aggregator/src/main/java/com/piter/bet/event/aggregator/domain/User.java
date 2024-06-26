package com.piter.bet.event.aggregator.domain;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public record User(
    @NotBlank
    String firstName,
    @NotBlank
    String lastName,
    @NotBlank
    String nickname
) {

}
