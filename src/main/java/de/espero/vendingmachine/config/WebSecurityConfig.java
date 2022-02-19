package de.espero.vendingmachine.config;

import de.espero.vendingmachine.model.db.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static de.espero.vendingmachine.controller.ProductController.BUY_ROUTE;
import static de.espero.vendingmachine.controller.ProductController.PRODUCT_ROUTE;
import static de.espero.vendingmachine.controller.UserController.DEPOSIT_ROUTE;
import static de.espero.vendingmachine.controller.UserController.RESET_ROUTE;
import static de.espero.vendingmachine.controller.UserController.USER_ROUTE;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;
    private final AuthenticationSuccessHandler sessionAuthenticationSuccessHandler;
    private final SessionLogoutHandler sessionLogoutHandler;
    private final SessionRegistry sessionRegistry;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement().maximumSessions(Integer.MAX_VALUE).sessionRegistry(sessionRegistry)
                .and().and().csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, USER_ROUTE).permitAll()
                .antMatchers(HttpMethod.POST, DEPOSIT_ROUTE).hasRole(Role.BUYER.name())
                .antMatchers(HttpMethod.POST, RESET_ROUTE).hasRole(Role.BUYER.name())
                .antMatchers(HttpMethod.POST, BUY_ROUTE).hasRole(Role.BUYER.name())
                .antMatchers(HttpMethod.POST, PRODUCT_ROUTE).hasRole(Role.SELLER.name())
                .antMatchers(HttpMethod.PUT, PRODUCT_ROUTE).hasRole(Role.SELLER.name())
                .antMatchers(HttpMethod.DELETE, PRODUCT_ROUTE).hasRole(Role.SELLER.name())
                .anyRequest().authenticated().and()
                .exceptionHandling()
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                .and().logout().logoutSuccessHandler(sessionLogoutHandler)
                .and().formLogin().successHandler(sessionAuthenticationSuccessHandler)
                .failureHandler(new SimpleUrlAuthenticationFailureHandler());
    }

}
