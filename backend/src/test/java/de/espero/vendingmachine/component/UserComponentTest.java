package de.espero.vendingmachine.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.espero.vendingmachine.model.Coin;
import de.espero.vendingmachine.model.db.Role;
import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.dto.UserResponse;
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

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserComponentTest {
    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    private MockMvc mvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    private User buyer = new User(1L, "buyer", "pass", 0, Set.of(Role.BUYER));

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        userRepository.save(buyer);
    }

    @WithMockUser
    @Test
    void testDepositCoin_whenUserIsNotBuyer_return403() throws Exception {
        mvc.perform(post("/deposit/FIVE")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDepositCoin_whenCoinIsNotFound_return400() throws Exception {
        mvc.perform(post("/deposit/XXX").with(user(buyer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testDepositCoin_whenRequestOk_returnUpdatedUser() throws Exception {
        final int value = 5;
        MvcResult result = mvc.perform(post(String.format("/deposit/%s", Coin.findByValue(value)))
                .with(user(buyer))).andExpect(status().isOk()).andReturn();

        UserResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

        assertEquals(value, response.deposit());
    }

    @Test
    void testDepositCoin_whenTwoDeposits_returnCorrectResult() throws Exception {
        final int value = 5;
        mvc.perform(post(String.format("/deposit/%s", Coin.findByValue(value)))
                .with(user(buyer))).andExpect(status().isOk()).andReturn();
        MvcResult result = mvc.perform(post(String.format("/deposit/%s", Coin.findByValue(value)))
                .with(user(buyer))).andExpect(status().isOk()).andReturn();

        UserResponse response = objectMapper.readValue(result.getResponse().getContentAsString(), UserResponse.class);

        assertEquals(value * 2, response.deposit());
    }
}
