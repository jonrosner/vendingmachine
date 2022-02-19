package de.espero.vendingmachine.service;

import de.espero.vendingmachine.model.Coin;
import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.db.factory.UserFactory;
import de.espero.vendingmachine.model.dto.RegisterUserRequest;
import de.espero.vendingmachine.model.dto.UpdateUserRequest;
import de.espero.vendingmachine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@RequiredArgsConstructor
@Validated
public class UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;
    private final ChangeService changeService;
    private final AuthService authService;

    public User registerUser(final RegisterUserRequest registerUserRequest) {
        User newUser = userFactory.buildFromRegisterRequest(registerUserRequest);
        return userRepository.save(newUser);
    }

    public User updateUser(final UpdateUserRequest updateRequest, final User user) {
        User newUser = userFactory.buildFromUpdateRequest(user, updateRequest);
        authService.clearAuthentication();
        return userRepository.save(newUser);
    }

    public void deleteUser(final User user) {
        authService.clearAuthentication();
        userRepository.delete(user);
    }

    public User depositCoin(final User user, final Coin coin) {
        User updatedUser = userFactory.buildFromDepositRequest(user, coin);
        authService.updateSecurityContext(updatedUser);
        return userRepository.save(updatedUser);
    }

    public User updateUserPurchaseProduct(final User user) {
        User updatedUser = user.withDeposit(0);
        authService.updateSecurityContext(updatedUser);
        return userRepository.save(updatedUser);
    }

    public List<Coin> resetDeposit(final User user) {
        List<Coin> result = changeService.calculateChange(user.getDeposit());
        User updatedUser = userRepository.save(user.withDeposit(0));
        authService.updateSecurityContext(updatedUser);
        return result;
    }

}
