package de.espero.vendingmachine.model.db;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.springframework.validation.annotation.Validated;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Positive;

@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED, force = true)
@AllArgsConstructor
@RequiredArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @With
    private final int amountAvailable;
    // cost in cents
    private final int cost;
    private final String productName;
    private final long sellerId;

    @Validated
    public long calculateCost(@Positive int amount) {
        return cost * amount;
    }

    public boolean isInStock(@Positive int requestedAmount) {
        return amountAvailable - requestedAmount >= 0;
    }

}
