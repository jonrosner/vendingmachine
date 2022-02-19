package de.espero.vendingmachine.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.espero.vendingmachine.model.db.User;
import de.espero.vendingmachine.model.dto.UserResponse;
import de.espero.vendingmachine.model.dto.factory.UserResponseFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Component
public class SessionAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SessionRegistry sessionRegistry;
    private final ObjectMapper objectMapper;
    private final UserResponseFactory userResponseFactory;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        final List<SessionInformation> sessions = sessionRegistry.getAllSessions(authentication.getPrincipal(), false);
        if (sessions.size() > 1) {
            if (response.isCommitted()) {
                return;
            }
            response.addHeader("x-multiple-sessions", "true");
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
        final User user = (User) authentication.getPrincipal();
        final UserResponse userResponse = userResponseFactory.buildFromUser(user);
        response.getOutputStream().print(objectMapper.writeValueAsString(userResponse));

        clearAuthenticationAttributes(request);
    }

}
