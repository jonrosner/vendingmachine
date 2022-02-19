package de.espero.vendingmachine.model.dto;

import java.util.Set;

public record UserResponse(
        long id,
        String username,
        long deposit,
        Set<String> authorities
) {
}
