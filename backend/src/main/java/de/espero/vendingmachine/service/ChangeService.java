package de.espero.vendingmachine.service;

import de.espero.vendingmachine.model.Coin;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
public class ChangeService {

    public List<Coin> calculateChange(final long depositAfterCostSubtracted) {
        Assert.isTrue(depositAfterCostSubtracted >= 0, "Deposit after subtraction must be positive");

        final List<Integer> orderedCoins = Arrays.stream(Coin.values()).map(coin -> coin.value)
                .sorted(Comparator.reverseOrder()).toList();
        final List<Coin> result = new ArrayList<>();
        long remainingDeposit = depositAfterCostSubtracted;

        for (final int coin : orderedCoins) {
            while (remainingDeposit >= coin) {
                remainingDeposit -= coin;
                result.add(Coin.findByValue(coin));
            }
        }

        return result;
    }
}
