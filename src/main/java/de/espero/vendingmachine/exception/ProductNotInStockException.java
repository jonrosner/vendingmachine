package de.espero.vendingmachine.exception;

public class ProductNotInStockException extends RuntimeException {
    public ProductNotInStockException(int requestedAmount, int availableAmount) {
        super(String.format("There are not enough products in stock. Requested amount: %s, amount available: %s",
                requestedAmount, availableAmount));
    }
}
