package de.espero.vendingmachine.exception;

public class NotEnoughFundsException extends RuntimeException {
    public NotEnoughFundsException(long totalCost, long deposit) {
        super(String.format("Current funds are not enough to purchase these products. Required funds: %s, current funds: %s",
                totalCost, deposit));
    }
}
