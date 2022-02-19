package de.espero.vendingmachine.exception;

import de.espero.vendingmachine.model.db.Role;

import java.util.Arrays;

public class RoleNotFoundException extends RuntimeException {
    public RoleNotFoundException(String name) {
        super(String.format("Unknown role: %s - allowed roles are %s", name, Arrays.stream(Role.values()).toList()));
    }
}
