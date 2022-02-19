package de.espero.vendingmachine.model.db.factory;

import de.espero.vendingmachine.model.db.Product;
import de.espero.vendingmachine.model.dto.CreateProductRequest;
import org.springframework.stereotype.Component;

@Component
public class ProductFactory {

    public Product buildFromCreateRequest(CreateProductRequest createProductRequest, Long sellerId) {
        return new Product(
                createProductRequest.amountAvailable(),
                createProductRequest.cost(),
                createProductRequest.productName(),
                sellerId);
    }

    public Product buildFromUpdateRequest(Product oldProduct, CreateProductRequest updateProductRequest) {
        return new Product(
                oldProduct.getId(),
                updateProductRequest.amountAvailable(),
                updateProductRequest.cost(),
                updateProductRequest.productName(),
                oldProduct.getSellerId());
    }

    public Product buildFromBuyRequest(Product oldProduct, int amountPurchased) {
        return new Product(
                oldProduct.getId(),
                oldProduct.getAmountAvailable() - amountPurchased,
                oldProduct.getCost(),
                oldProduct.getProductName(),
                oldProduct.getSellerId());
    }
}
