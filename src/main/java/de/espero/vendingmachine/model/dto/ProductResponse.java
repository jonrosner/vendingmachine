package de.espero.vendingmachine.model.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record ProductResponse(
        @NotNull Long id,
        @Positive int cost,
        @NotNull String productName,
        long sellerId
) {
}
