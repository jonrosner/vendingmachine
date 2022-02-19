package de.espero.vendingmachine.model;

import de.espero.vendingmachine.exception.CoinNotFoundException;
import lombok.AllArgsConstructor;

import java.util.Arrays;

@AllArgsConstructor
public enum Coin {
    FIVE(5),
    TEN(10),
    TWENTY(20),
    FIFTY(50),
    HUNDRED(100);

    public final int value;

    public static Coin findByName(String name) {
        return Arrays.stream(values()).filter(value -> value.name().equals(name)).findFirst()
                .orElseThrow(() -> new CoinNotFoundException(name));
    }

    public static Coin findByValue(int value) {
        return Arrays.stream(values()).filter(coin -> coin.value == value).findFirst()
                .orElseThrow(() -> new CoinNotFoundException(value));
    }
}
