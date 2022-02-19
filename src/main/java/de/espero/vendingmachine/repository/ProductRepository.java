package de.espero.vendingmachine.repository;

import de.espero.vendingmachine.model.db.Product;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, Long> {
}
