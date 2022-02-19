package de.espero.vendingmachine.model.dto.factory;

import de.espero.vendingmachine.model.Coin;
import de.espero.vendingmachine.model.db.Product;
import de.espero.vendingmachine.model.dto.BuyResponse;
import de.espero.vendingmachine.model.dto.ProductResponse;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class BuyResponseFactory {

    public BuyResponse buildFromBuyRequest(final Product productBought, final int amount, final List<Coin> change) {
        return new BuyResponse(
                productBought.calculateCost(amount),
                Collections.nCopies(amount, buildFromProduct(productBought)),
                change
        );
    }

    private ProductResponse buildFromProduct(final Product product) {
        return new ProductResponse(product.getId(), product.getCost(), product.getProductName(), product.getSellerId());
    }

}
