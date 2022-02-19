package de.espero.vendingmachine.model.db;

import de.espero.vendingmachine.exception.RoleNotFoundException;

import java.util.Arrays;

public enum Role {
    BUYER, SELLER;

    public static Role findByName(String name) {
        return Arrays.stream(values()).filter(value -> value.name().equals(name)).findFirst()
                .orElseThrow(() -> new RoleNotFoundException(name));
    }
}
