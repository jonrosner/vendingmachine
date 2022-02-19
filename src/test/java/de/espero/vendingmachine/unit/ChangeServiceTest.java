package de.espero.vendingmachine.unit;

import de.espero.vendingmachine.model.Coin;
import de.espero.vendingmachine.service.ChangeService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ChangeServiceTest {

    private ChangeService changeService = new ChangeService();

    @Test
    void testCalculateChange_whenDepositIs100_returnCorrectResult() {
        List<Coin> expectedResult = List.of(Coin.HUNDRED);
        List<Coin> actualResult = changeService.calculateChange(100);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCalculateChange_whenDepositIs9_returnCorrectResult() {
        List<Coin> expectedResult = List.of(Coin.FIVE);
        List<Coin> actualResult = changeService.calculateChange(9);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCalculateChange_whenDepositIs16_returnCorrectResult() {
        List<Coin> expectedResult = List.of(Coin.TEN, Coin.FIVE);
        List<Coin> actualResult = changeService.calculateChange(16);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCalculateChange_whenDepositIs185_returnCorrectResult() {
        List<Coin> expectedResult = List.of(Coin.HUNDRED, Coin.FIFTY, Coin.TWENTY, Coin.TEN, Coin.FIVE);
        List<Coin> actualResult = changeService.calculateChange(185);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCalculateChange_whenDepositIs0_returnCorrectResult() {
        List<Coin> expectedResult = List.of();
        List<Coin> actualResult = changeService.calculateChange(0);
        assertEquals(expectedResult, actualResult);
    }

    @Test
    void testCalculateChange_whenDepositIsNegative_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> changeService.calculateChange(-1));
    }

}
