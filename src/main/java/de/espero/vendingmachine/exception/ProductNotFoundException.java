package de.espero.vendingmachine.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long productId) {
        super(String.format("No product found for ID: %s", productId));
    }
}
