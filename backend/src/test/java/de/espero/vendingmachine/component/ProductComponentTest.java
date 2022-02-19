package de.espero.vendingmachine.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.espero.vendingmachine.controller.ProductController;
import de.espero.vendingmachine.model.Coin;
import de.espero.vendingmachine.model.db.Product;
import de.espero.vendingmachine.model.db.Role;
import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.dto.BuyRequest;
import de.espero.vendingmachine.model.dto.BuyResponse;
import de.espero.vendingmachine.model.dto.CreateProductRequest;
import de.espero.vendingmachine.model.dto.factory.BuyResponseFactory;
import de.espero.vendingmachine.repository.ProductRepository;
import de.espero.vendingmachine.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import java.util.Set;

import static de.espero.vendingmachine.TestUtils.buildProductWithId;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class ProductComponentTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuyResponseFactory buyResponseFactory;

    private MockMvc mvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User seller = new User(1L, "seller", "pass", 0, Set.of(Role.SELLER));
    private User buyer = new User(2L, "buyer", "pass", 100, Set.of(Role.BUYER));

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        userRepository.save(seller);
        userRepository.save(buyer);
    }

    @WithMockUser
    @Test
    void testGetAllProducts_whenUserAuthenticated_returnProducts() throws Exception {
        final Product product = productRepository.save(buildProductWithId());
        final String expectedBody = objectMapper.writeValueAsString(List.of(product));

        MvcResult result = mvc.perform(get(ProductController.PRODUCT_ROUTE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andReturn();

        assertEquals(expectedBody, result.getResponse().getContentAsString());
    }

    @WithMockUser
    @Test
    void testGetProduct_whenProductExists_returnProduct() throws Exception {
        final Product product = productRepository.save(buildProductWithId());
        final String expectedBody = objectMapper.writeValueAsString(product);

        MvcResult result = mvc.perform(get(String.format("%s/%s", ProductController.PRODUCT_ROUTE, 3)))
                .andExpect(status().isOk()).andReturn();

        assertEquals(expectedBody, result.getResponse().getContentAsString());
    }

    @WithMockUser
    @Test
    void testGetProduct_whenProductDoesNotExist_return404() throws Exception {
        mvc.perform(get(String.format("%s/%s", ProductController.PRODUCT_ROUTE, 1)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProduct_whenUserNotAuthenticated_return401() throws Exception {
        mvc.perform(get(String.format("%s/%s", ProductController.PRODUCT_ROUTE, 1)))
                .andExpect(status().isUnauthorized());
    }

    @WithMockUser
    @Test
    void testPostProduct_whenUserNotSeller_return403() throws Exception {
        mvc.perform(post(ProductController.PRODUCT_ROUTE)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @WithMockUser(roles = "SELLER")
    @Test
    void testPostProduct_whenBodyNotValid_return400() throws Exception {
        final CreateProductRequest req = new CreateProductRequest(1, 1, null);
        mvc.perform(post(ProductController.PRODUCT_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testPostProduct_whenRequestOk_returnNewProduct() throws Exception {
        final CreateProductRequest req = new CreateProductRequest(1, 1, "Product_A");
        final Product product = new Product(3L, 1, 1, "Product_A", 1);
        final String expectedResponse = objectMapper.writeValueAsString(product);
        final MvcResult result = mvc.perform(post(ProductController.PRODUCT_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(user(seller)))
                .andExpect(status().isOk()).andReturn();
        assertEquals(expectedResponse, result.getResponse().getContentAsString());
    }

    @Test
    void testPutProduct_whenProductNotBySameUser_return404() throws Exception {
        final CreateProductRequest req = new CreateProductRequest(1, 1, "Product_A");
        final Product product = new Product(1, 1, "Product_B", 999);
        final Product productWithId = productRepository.save(product);

        mvc.perform(put(String.format("%s/%s", ProductController.PRODUCT_ROUTE, productWithId.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(user(seller)))
                .andExpect(status().isNotFound());
    }

    @Test
    void testPutProduct_whenRequestOk_returnUpdatedProduct() throws Exception {
        final Product product = new Product(1, 1, "Product_A", 1);
        final Product productWithId = productRepository.save(product);
        final Product updatedProduct = productWithId.withAmountAvailable(10);
        final CreateProductRequest req = new CreateProductRequest(10, 1, "Product_A");
        final String expectedResponse = objectMapper.writeValueAsString(updatedProduct);

        final MvcResult result = mvc.perform(put(String.format("%s/%s", ProductController.PRODUCT_ROUTE, productWithId.getId()))
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))
                .with(user(seller)))
                .andExpect(status().isOk()).andReturn();
        assertEquals(expectedResponse, result.getResponse().getContentAsString());
    }

    @Test
    void testDeleteProduct_whenProductExists_return200() throws Exception {
        final Product product = new Product(1, 1, "Product_A", 1);
        final Product productWithId = productRepository.save(product);

        mvc.perform(delete(String.format("%s/%s", ProductController.PRODUCT_ROUTE, productWithId.getId()))
                .with(user(seller)))
                .andExpect(status().isOk());
    }

    @Test
    void testBuyProducts_whenUserDoesNotHaveEnoughFunds_return400() throws Exception {
        final Product product = new Product(2, 1000, "Product_A", 1);
        final Product productWithId = productRepository.save(product);
        final BuyRequest buyRequest = new BuyRequest(productWithId.getId(), 2);

        MvcResult result = mvc.perform(post(ProductController.BUY_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buyRequest))
                .with(user(buyer)))
                .andExpect(status().isBadRequest()).andReturn();

        assertThat(result.getResponse().getContentAsString(), containsString("NotEnoughFundsException"));
    }

    @Test
    void testBuyProducts_whenProductNotInStock_return400() throws Exception {
        final Product product = new Product(2, 1, "Product_A", 1);
        final Product productWithId = productRepository.save(product);
        final BuyRequest buyRequest = new BuyRequest(productWithId.getId(), 10);

        MvcResult result = mvc.perform(post(ProductController.BUY_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buyRequest))
                .with(user(buyer)))
                .andExpect(status().isBadRequest()).andReturn();

        assertThat(result.getResponse().getContentAsString(), containsString("ProductNotInStockException"));
    }

    @Test
    void testBuyProducts_whenRequestOk_return200() throws Exception {
        final Product product = new Product(2, 1, "Product_A", 1);
        final Product productWithId = productRepository.save(product);
        final BuyRequest buyRequest = new BuyRequest(productWithId.getId(), 2);
        final List<Coin> change = List.of(Coin.FIFTY, Coin.TWENTY, Coin.TWENTY, Coin.FIVE);
        final BuyResponse expectedResponse = buyResponseFactory.buildFromBuyRequest(productWithId, buyRequest.amount(), change);

        MvcResult result = mvc.perform(post(ProductController.BUY_ROUTE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(buyRequest))
                .with(user(buyer)))
                .andExpect(status().isOk()).andReturn();

        assertEquals(objectMapper.writeValueAsString(expectedResponse), result.getResponse().getContentAsString());

        final Product updatedProduct = productRepository.findById(productWithId.getId()).get();
        final User updatedUser = userRepository.findByUsername(buyer.getUsername()).get();

        assertEquals(product.getAmountAvailable() - buyRequest.amount(), updatedProduct.getAmountAvailable());
        assertEquals(0, updatedUser.getDeposit());
    }

}
