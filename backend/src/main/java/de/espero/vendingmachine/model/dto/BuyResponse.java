package de.espero.vendingmachine.model.dto;

import de.espero.vendingmachine.model.Coin;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

public record BuyResponse(
        @Positive long totalSpent,
        @NotEmpty List<ProductResponse> productsPurchased,
        @NotNull List<Coin> change
) {
}
