package org.bhuvanesh.ecommerce.repository;

import org.bhuvanesh.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
