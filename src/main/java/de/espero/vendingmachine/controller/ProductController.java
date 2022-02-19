package de.espero.vendingmachine.controller;

import de.espero.vendingmachine.model.db.Product;
import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.dto.BuyRequest;
import de.espero.vendingmachine.model.dto.BuyResponse;
import de.espero.vendingmachine.model.dto.CreateProductRequest;
import de.espero.vendingmachine.service.AuthService;
import de.espero.vendingmachine.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {
    public static final String PRODUCT_ROUTE = "/product";
    public static final String BUY_ROUTE = "/buy";

    private final ProductService productService;
    private final AuthService authService;

    @GetMapping(value = "/product/{id}", consumes = MediaType.ALL_VALUE)
    public Product getProduct(@PathVariable final Long id) {
        return productService.getProduct(id);
    }

    @GetMapping(value = PRODUCT_ROUTE, consumes = MediaType.ALL_VALUE)
    public Iterable<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    @PostMapping(value = PRODUCT_ROUTE)
    public Product postProduct(@Valid @RequestBody final CreateProductRequest createProductRequest) {
        final User user = authService.getCurrentUser();
        return productService.addProduct(createProductRequest, user.getId());
    }

    @PutMapping(value = "/product/{id}")
    public Product putProduct(@PathVariable final Long id, @RequestBody final CreateProductRequest updateRequest) {
        final User user = authService.getCurrentUser();
        return productService.updateProduct(updateRequest, id, user.getId());
    }

    @DeleteMapping(value = "/product/{id}", consumes = MediaType.ALL_VALUE)
    public void deleteProduct(@PathVariable final Long id) {
        final User user = authService.getCurrentUser();
        productService.deleteProduct(id, user.getId());
    }

    @PostMapping(value = BUY_ROUTE)
    public BuyResponse buyProducts(@Valid @RequestBody BuyRequest buyRequest) {
        final User user = authService.getCurrentUser();
        return productService.buyProducts(user, buyRequest);
    }
}
