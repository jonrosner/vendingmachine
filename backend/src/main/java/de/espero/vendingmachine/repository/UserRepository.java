package de.espero.vendingmachine.repository;

import de.espero.vendingmachine.model.db.User;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
