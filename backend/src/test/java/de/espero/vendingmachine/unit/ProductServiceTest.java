package de.espero.vendingmachine.unit;

import de.espero.vendingmachine.exception.NotEnoughFundsException;
import de.espero.vendingmachine.exception.ProductNotFoundException;
import de.espero.vendingmachine.exception.ProductNotInStockException;
import de.espero.vendingmachine.model.db.Product;
import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.db.factory.ProductFactory;
import de.espero.vendingmachine.model.dto.BuyRequest;
import de.espero.vendingmachine.model.dto.BuyResponse;
import de.espero.vendingmachine.model.dto.CreateProductRequest;
import de.espero.vendingmachine.model.dto.factory.BuyResponseFactory;
import de.espero.vendingmachine.repository.ProductRepository;
import de.espero.vendingmachine.service.AuthService;
import de.espero.vendingmachine.service.ChangeService;
import de.espero.vendingmachine.service.ProductService;
import de.espero.vendingmachine.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.util.Streamable;

import java.util.Optional;

import static de.espero.vendingmachine.TestUtils.buildBuyRequest;
import static de.espero.vendingmachine.TestUtils.buildBuyResponse;
import static de.espero.vendingmachine.TestUtils.buildCreateProductRequest;
import static de.espero.vendingmachine.TestUtils.buildMockProducts;
import static de.espero.vendingmachine.TestUtils.buildProductWithAmountAvailable;
import static de.espero.vendingmachine.TestUtils.buildProductWithId;
import static de.espero.vendingmachine.TestUtils.buildUser;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(value = MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductFactory productFactory;
    @Mock
    private UserService userService;
    @Mock
    private BuyResponseFactory buyResponseFactory;
    @Mock
    private ChangeService changeService;
    @Mock
    private AuthService authService;

    @InjectMocks
    private ProductService productService;

    @Test
    void testGetAllProducts_whenDatabaseOk_returnProducts() {
        // GIVEN
        final var expectedProducts = buildMockProducts();

        // WHEN
        Mockito.when(productRepository.findAll()).thenReturn(expectedProducts);

        Iterable<Product> actualProducts = productService.getAllProducts();

        // THEN
        assertEquals(expectedProducts, Streamable.of(actualProducts).toList());
    }

    @Test
    void testGetProduct_whenProductExistsInDatabase_returnProduct() {
        // GIVEN
        final var expectedProduct = buildMockProducts().get(0);

        // WHEN
        Mockito.when(productRepository.findById(expectedProduct.getId())).thenReturn(Optional.of(expectedProduct));

        Product actualProduct = productService.getProduct(expectedProduct.getId());

        // THEN
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void testGetProduct_whenProductDoesNotExistInDatabase_throwProductNotFoundException() {
        // GIVEN
        final long productId = 0;

        // WHEN
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // THEN
        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(productId));
    }

    @Test
    void testAddProduct_whenProductFactoryOk_returnCreatedProduct() {
        // GIVEN
        final var createProductRequest = buildCreateProductRequest();
        final var expectedProduct = buildMockProducts().get(0);
        final long sellerId = 1;

        // WHEN
        Mockito.when(productFactory.buildFromCreateRequest(createProductRequest, sellerId)).thenReturn(expectedProduct);
        Mockito.when(productRepository.save(expectedProduct)).thenReturn(expectedProduct);

        Product actualProduct = productService.addProduct(createProductRequest, sellerId);

        // THEN
        assertEquals(expectedProduct, actualProduct);
    }

    @Test
    void testAddProduct_whenProductRepositoryThrows_propagateException() {
        // GIVEN
        final var createProductRequest = buildCreateProductRequest();
        final long sellerId = 1;

        // WHEN
        Mockito.when(productRepository.save(any())).thenThrow(new RuntimeException());

        // THEN
        assertThrows(RuntimeException.class, () -> productService.addProduct(createProductRequest, sellerId));
    }

    @Test
    void testUpdateProduct_whenProductDoesNotExist_throwProductNotFoundException() {
        // GIVEN
        final long productId = 0;
        final long userId = 1;

        // WHEN
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // THEN
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(null, productId, userId));
    }

    @Test
    void testUpdateProduct_whenProductDoesNotBelongToUser_throwProductNotFoundException() {
        // GIVEN
        final long productId = 0;
        final Product product = buildMockProducts().get(0);
        final long userId = 999;

        // WHEN
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // THEN
        assertThrows(ProductNotFoundException.class, () -> productService.updateProduct(null, productId, userId));
    }

    @Test
    void testUpdateProduct_whenRequestOk_returnUpdatedProduct() {
        // GIVEN
        final long productId = 0;
        final long userId = 1;
        final Product initialProduct = buildProductWithId();
        final CreateProductRequest createProductRequest = buildCreateProductRequest();
        final Product expectedProduct = new Product(
                productId,
                createProductRequest.amountAvailable(),
                createProductRequest.cost(),
                createProductRequest.productName(),
                userId);

        // WHEN
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(initialProduct));
        Mockito.when(productFactory.buildFromUpdateRequest(initialProduct, createProductRequest)).thenReturn(expectedProduct);
        Mockito.when(productRepository.save(expectedProduct)).thenReturn(expectedProduct);

        final Product actualProduct = productService.updateProduct(createProductRequest, productId, userId);

        // THEN
        assertEquals(actualProduct, expectedProduct);
    }

    @Test
    void testDeleteProduct_whenProductDoesNotExist_throwProductNotFoundException() {
        // GIVEN
        final long productId = 0;
        final long userId = 1;

        // WHEN
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // THEN
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId, userId));
    }

    @Test
    void testDeleteProduct_whenProductDoesNotBelongToUser_throwProductNotFoundException() {
        // GIVEN
        final long productId = 0;
        final Product product = buildProductWithId();
        final long userId = 999;

        // WHEN
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // THEN
        assertThrows(ProductNotFoundException.class, () -> productService.deleteProduct(productId, userId));
    }

    @Test
    void testDeleteProduct_whenProductExists_doNotThrow() {
        final long productId = 0;
        final long userId = 1;
        final Product product = buildProductWithId();

        // WHEN
        Mockito.when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // THEN
        assertDoesNotThrow(() -> productService.deleteProduct(productId, userId));
    }

    @Test
    void testBuyProduct_whenProductDoesNotExist_throwProductNotFoundException() {
        // GIVEN
        final BuyRequest buyRequest = buildBuyRequest();
        final User user = buildUser();

        // WHEN
        Mockito.when(productRepository.findById(buyRequest.productId())).thenReturn(Optional.empty());

        // THEN
        assertThrows(ProductNotFoundException.class, () -> productService.buyProducts(user, buyRequest));
    }

    @Test
    void testBuyProduct_whenUserDoesNotHaveEnoughFunds_throwNotEnoughFundsException() {
        // GIVEN
        final BuyRequest buyRequest = buildBuyRequest();
        final Product product = buildProductWithId();
        final User user = buildUser();

        // WHEN
        Mockito.when(productRepository.findById(buyRequest.productId())).thenReturn(Optional.of(product));

        // THEN
        assertThrows(NotEnoughFundsException.class, () -> productService.buyProducts(user, buyRequest));
    }

    @Test
    void testBuyProduct_whenProductNotInStock_throwProductNotInStockException() {
        // GIVEN
        final BuyRequest buyRequest = buildBuyRequest();
        final Product product = buildProductWithId();
        final User user = buildUser().withDeposit(1000);

        // WHEN
        Mockito.when(productRepository.findById(buyRequest.productId())).thenReturn(Optional.of(product));

        // THEN
        assertThrows(ProductNotInStockException.class, () -> productService.buyProducts(user, buyRequest));
    }

    @Test
    void testBuyProduct_whenRequestOk_returnCorrectResponse() {
        // GIVEN
        final BuyRequest buyRequest = buildBuyRequest();
        final Product product = buildProductWithAmountAvailable();
        final User user = buildUser().withDeposit(1000);
        final BuyResponse expectedResult = buildBuyResponse();

        // WHEN
        Mockito.when(productRepository.findById(buyRequest.productId())).thenReturn(Optional.of(product));
        Mockito.when(changeService.calculateChange(anyLong()))
                .thenReturn(expectedResult.change());
        Mockito.when(buyResponseFactory.buildFromBuyRequest(product, buyRequest.amount(), expectedResult.change()))
                .thenReturn(expectedResult);
        Mockito.when(userService.updateUserPurchaseProduct(any())).thenReturn(user);

        final BuyResponse actualResult = productService.buyProducts(user, buyRequest);

        // THEN
        assertEquals(expectedResult, actualResult);
    }
}
