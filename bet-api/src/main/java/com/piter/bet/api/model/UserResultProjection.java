package com.piter.bet.api.model;

import com.piter.api.commons.domain.User;

public record UserResultProjection(User user, Long points) {
}
