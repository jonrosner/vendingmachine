package de.espero.vendingmachine.service;

import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public User getCurrentUser() {
        final SecurityContext ctx = SecurityContextHolder.getContext();
        final User user = (User) ctx.getAuthentication().getPrincipal();
        return userRepository.findById(user.getId()).get();
    }

    public void updateSecurityContext(final User updatedUser) {
        SecurityContext ctx = SecurityContextHolder.getContext();
        final Authentication updatedAuthentication = new UsernamePasswordAuthenticationToken(updatedUser,
                updatedUser.getPassword(), updatedUser.getAuthorities());
        ctx.setAuthentication(updatedAuthentication);
    }

    public void clearAuthentication() {
        SecurityContext ctx = SecurityContextHolder.getContext();
        ctx.getAuthentication().setAuthenticated(false);
    }

}
