package de.espero.vendingmachine.model.dto.factory;

import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.dto.UserResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class UserResponseFactory {

    public UserResponse buildFromUser(User user) {
        Set<String> authorities = user.getAuthorities().stream().map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());
        return new UserResponse(user.getId(), user.getUsername(), user.getDeposit(), authorities);
    }
}
