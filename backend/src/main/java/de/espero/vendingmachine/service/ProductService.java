package de.espero.vendingmachine.service;

import de.espero.vendingmachine.exception.NotEnoughFundsException;
import de.espero.vendingmachine.exception.ProductNotFoundException;
import de.espero.vendingmachine.exception.ProductNotInStockException;
import de.espero.vendingmachine.model.Coin;
import de.espero.vendingmachine.model.db.Product;
import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.db.factory.ProductFactory;
import de.espero.vendingmachine.model.dto.BuyRequest;
import de.espero.vendingmachine.model.dto.BuyResponse;
import de.espero.vendingmachine.model.dto.CreateProductRequest;
import de.espero.vendingmachine.model.dto.factory.BuyResponseFactory;
import de.espero.vendingmachine.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductFactory productFactory;
    private final UserService userService;
    private final BuyResponseFactory buyResponseFactory;
    private final ChangeService changeService;

    public Product getProduct(@NotNull final Long productId) {
        return productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
    }

    public Iterable<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Product addProduct(@NotNull final CreateProductRequest createProductRequest,
                              @NotNull final Long sellerId) {
        Product newProduct = productFactory.buildFromCreateRequest(createProductRequest, sellerId);
        return productRepository.save(newProduct);
    }

    public Product updateProduct(@NotNull final CreateProductRequest updateProductRequest,
                                 @NotNull final Long productId, @NotNull final Long userId) {
        Product oldProduct = productRepository.findById(productId).orElseThrow(() ->
                new ProductNotFoundException(productId));
        if (oldProduct.getSellerId() != userId) {
            throw new ProductNotFoundException(productId);
        }
        return productRepository.save(productFactory.buildFromUpdateRequest(oldProduct, updateProductRequest));
    }

    public void deleteProduct(@NotNull final Long productId, @NotNull final Long userId) {
        Product oldProduct = productRepository.findById(productId).orElseThrow(() ->
                new ProductNotFoundException(productId));
        if (oldProduct.getSellerId() != userId) {
            throw new ProductNotFoundException(productId);
        }
        productRepository.delete(oldProduct);
    }

    @Transactional
    public BuyResponse buyProducts(@NotNull final User user, @NotNull final BuyRequest buyRequest) {
        Product product = productRepository.findById(buyRequest.productId()).orElseThrow(() ->
                new ProductNotFoundException(buyRequest.productId()));
        final long totalCost = product.calculateCost(buyRequest.amount());
        if (!user.hasEnoughFunds(totalCost)) {
            throw new NotEnoughFundsException(totalCost, user.getDeposit());
        }
        if (!product.isInStock(buyRequest.amount())) {
            throw new ProductNotInStockException(buyRequest.amount(), product.getAmountAvailable());
        }

        final List<Coin> change = changeService.calculateChange(user.getDeposit() - totalCost);
        productRepository.save(productFactory.buildFromBuyRequest(product, buyRequest.amount()));
        userService.updateUserPurchaseProduct(user);

        return buyResponseFactory.buildFromBuyRequest(product, buyRequest.amount(), change);
    }

}
