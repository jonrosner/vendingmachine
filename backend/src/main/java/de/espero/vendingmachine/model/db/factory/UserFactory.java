package de.espero.vendingmachine.model.db.factory;

import de.espero.vendingmachine.model.Coin;
import de.espero.vendingmachine.model.db.Role;
import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.dto.RegisterUserRequest;
import de.espero.vendingmachine.model.dto.UpdateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    public User buildFromRegisterRequest(RegisterUserRequest registerUserRequest) {
        return new User(
                registerUserRequest.username(),
                passwordEncoder.encode(registerUserRequest.password()),
                0L,
                Set.of(Role.findByName(registerUserRequest.authority())));
    }

    public User buildFromUpdateRequest(final User oldUser, final UpdateUserRequest updateUserRequest) {
        return new User(
                oldUser.getId(),
                updateUserRequest.username(),
                passwordEncoder.encode(updateUserRequest.password()),
                oldUser.getDeposit(),
                oldUser.getRoles());
    }

    public User buildFromDepositRequest(final User oldUser, final Coin depositCoin) {
        final Long newDeposit = oldUser.getDeposit() + depositCoin.value;
        return oldUser.withDeposit(newDeposit);
    }

}
