package de.espero.vendingmachine.config;

import de.espero.vendingmachine.exception.CoinNotFoundException;
import de.espero.vendingmachine.exception.NotEnoughFundsException;
import de.espero.vendingmachine.exception.ProductNotFoundException;
import de.espero.vendingmachine.exception.ProductNotInStockException;
import de.espero.vendingmachine.exception.RoleNotFoundException;
import de.espero.vendingmachine.exception.UserNotFoundException;
import de.espero.vendingmachine.model.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.OffsetDateTime;

@ControllerAdvice
public class ExceptionHandlerConfig {

    @ExceptionHandler(value = ProductNotFoundException.class)
    public ResponseEntity<ErrorResponse> productNotFoundException(ProductNotFoundException e) {
        return createErrorResponseEntity(HttpStatus.NOT_FOUND, e.getClass().getSimpleName(), e.getMessage());
    }

    @ExceptionHandler(value = NotEnoughFundsException.class)
    public ResponseEntity<ErrorResponse> notEnoughFundsException(NotEnoughFundsException e) {
        return createErrorResponseEntity(HttpStatus.BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
    }

    @ExceptionHandler(value = ProductNotInStockException.class)
    public ResponseEntity<ErrorResponse> productNotInStockException(ProductNotInStockException e) {
        return createErrorResponseEntity(HttpStatus.BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
    }

    @ExceptionHandler(value = RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> roleNotFoundException(RoleNotFoundException e) {
        return createErrorResponseEntity(HttpStatus.BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
    }

    @ExceptionHandler(value = CoinNotFoundException.class)
    public ResponseEntity<ErrorResponse> coinNotFoundException(CoinNotFoundException e) {
        return createErrorResponseEntity(HttpStatus.BAD_REQUEST, e.getClass().getSimpleName(), e.getMessage());
    }

    @ExceptionHandler(value = UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> userNotFoundException(UserNotFoundException e) {
        return createErrorResponseEntity(HttpStatus.NOT_FOUND, e.getClass().getSimpleName(), e.getMessage());
    }

    private ResponseEntity createErrorResponseEntity(final HttpStatus status, final String className, final String message) {
        ErrorResponse response = new ErrorResponse(OffsetDateTime.now(), status.value(), className, message);
        return new ResponseEntity(response, status);
    }

}
