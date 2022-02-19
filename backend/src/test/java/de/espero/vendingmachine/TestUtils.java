package de.espero.vendingmachine;

import de.espero.vendingmachine.model.Coin;
import de.espero.vendingmachine.model.db.Product;
import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.dto.BuyRequest;
import de.espero.vendingmachine.model.dto.BuyResponse;
import de.espero.vendingmachine.model.dto.CreateProductRequest;
import de.espero.vendingmachine.model.dto.ProductResponse;

import java.util.List;

public class TestUtils {

    public static CreateProductRequest buildCreateProductRequest() {
        return new CreateProductRequest(2, 2, "Product_Z");
    }

    public static List<Product> buildMockProducts() {
        return List.of(
                new Product(1, 1, "Product_A", 1),
                new Product(1, 1, "Product_B", 2)
        );
    }

    public static Product buildProductWithId() {
        return new Product(1L, 1, 1, "Product_A", 1);
    }

    public static Product buildProductWithAmountAvailable() {
        return new Product(1L, 100, 1, "Product_A", 1);
    }

    public static BuyRequest buildBuyRequest() {
        return new BuyRequest(1L, 2);
    }

    public static User buildUser() {
        return new User(1L, "user", "pass", 0, null);
    }

    public static BuyResponse buildBuyResponse() {
        return new BuyResponse(10,
                List.of(new ProductResponse(1L, 5, "Product_A", 2L)),
                List.of(Coin.FIVE));
    }
}
