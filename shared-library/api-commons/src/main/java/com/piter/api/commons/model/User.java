package com.piter.api.commons.model;

import jakarta.validation.constraints.NotBlank;

public record User(
    @NotBlank
    String firstName,
    @NotBlank
    String lastName,
    @NotBlank
    String nickname) {

}
