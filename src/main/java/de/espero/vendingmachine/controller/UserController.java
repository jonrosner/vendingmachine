package de.espero.vendingmachine.controller;

import de.espero.vendingmachine.model.Coin;
import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.dto.RegisterUserRequest;
import de.espero.vendingmachine.model.dto.UpdateUserRequest;
import de.espero.vendingmachine.model.dto.UserResponse;
import de.espero.vendingmachine.model.dto.factory.UserResponseFactory;
import de.espero.vendingmachine.service.AuthService;
import de.espero.vendingmachine.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {
    public static final String USER_ROUTE = "/user";
    public static final String DEPOSIT_ROUTE = "/deposit/{coinName}";
    public static final String RESET_ROUTE = "/reset";

    private final UserService userService;
    private final AuthService authService;
    private final UserResponseFactory userResponseFactory;

    @GetMapping(value = USER_ROUTE, consumes = MediaType.ALL_VALUE)
    public UserResponse getUser() {
        final User user = authService.getCurrentUser();
        return userResponseFactory.buildFromUser(user);
    }

    @PostMapping(value = USER_ROUTE)
    public UserResponse postUser(@Valid @RequestBody final RegisterUserRequest registerUserRequest) {
        User newUser = userService.registerUser(registerUserRequest);
        return userResponseFactory.buildFromUser(newUser);
    }

    @PutMapping(value = USER_ROUTE)
    public RedirectView putUser(@Valid @RequestBody final UpdateUserRequest updateRequest) {
        final User user = authService.getCurrentUser();

        User updatedUser = userService.updateUser(updateRequest, user);

        return new RedirectView("/login");
    }

    @DeleteMapping(value = USER_ROUTE, consumes = MediaType.ALL_VALUE)
    public RedirectView deleteUser() {
        final User user = authService.getCurrentUser();
        userService.deleteUser(user);

        return new RedirectView("/login");
    }

    @PostMapping(value = DEPOSIT_ROUTE, consumes = MediaType.ALL_VALUE)
    public UserResponse postDeposit(@PathVariable final String coinName) {
        final Coin coin = Coin.findByName(coinName);
        final User user = authService.getCurrentUser();

        final User updatedUser = userService.depositCoin(user, coin);

        return userResponseFactory.buildFromUser(updatedUser);
    }

    @PostMapping(value = RESET_ROUTE, consumes = MediaType.ALL_VALUE)
    public List<Coin> resetUserDeposit() {
        final User user = authService.getCurrentUser();

        final List<Coin> result = userService.resetDeposit(user);

        return result;
    }
}
