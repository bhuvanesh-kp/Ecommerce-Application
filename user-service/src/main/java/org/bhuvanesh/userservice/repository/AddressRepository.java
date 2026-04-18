package org.bhuvanesh.userservice.repository;

import org.bhuvanesh.userservice.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    Optional<Address> findByIdAndUserId(UUID id, UUID userId);
}
