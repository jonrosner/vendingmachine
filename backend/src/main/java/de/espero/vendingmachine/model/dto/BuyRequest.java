package de.espero.vendingmachine.model.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public record BuyRequest(
        @NotNull Long productId,
        @Min(1) int amount
) {
}
