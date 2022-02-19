package de.espero.vendingmachine.model.dto;

public record UpdateUserRequest(
        String username,
        String password
) {
}
