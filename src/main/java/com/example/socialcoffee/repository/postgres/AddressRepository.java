package com.example.socialcoffee.repository.postgres;

import com.example.socialcoffee.domain.postgres.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
