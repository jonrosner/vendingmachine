package de.espero.vendingmachine.model;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.OffsetDateTime;

public record ErrorResponse(
        @NotNull OffsetDateTime timestamp,
        @Positive int status,
        @NotNull String error,
        String message
) {
}
