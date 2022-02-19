package de.espero.vendingmachine.model.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public record RegisterUserRequest(
        @NotEmpty String username,
        @NotEmpty String password,
        @NotNull String authority
) {
}
