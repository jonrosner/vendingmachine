package de.espero.vendingmachine.exception;

import de.espero.vendingmachine.model.Coin;

import java.util.Arrays;

public class CoinNotFoundException extends RuntimeException {
    public CoinNotFoundException(String name) {
        super(String.format("Unknown coin: %s - allowed coins are %s", name, Arrays.stream(Coin.values()).toList()));
    }

    public CoinNotFoundException(int value) {
        super(String.format("No coin found with value: %s - allowed coins are %s", value,
                Arrays.stream(Coin.values()).toList()));
    }
}
