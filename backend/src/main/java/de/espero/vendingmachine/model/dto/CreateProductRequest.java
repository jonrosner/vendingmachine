package de.espero.vendingmachine.model.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

public record CreateProductRequest(
    @Positive Integer amountAvailable,
    @Positive Integer cost,
    @NotNull String productName
) {
}
