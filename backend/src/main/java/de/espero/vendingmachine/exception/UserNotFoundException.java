package de.espero.vendingmachine.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String username) {
        super(String.format("No user found for username: %s", username));
    }
}
