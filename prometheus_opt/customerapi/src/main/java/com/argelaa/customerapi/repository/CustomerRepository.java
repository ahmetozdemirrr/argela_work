package com.argelaa.customerapi.repository;

import com.argelaa.customerapi.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long>
{
    Optional<Customer> findByEmail(String email);
    List<Customer> findByLastName(String lastName);
    List<Customer> findByTotalSpentAmountGreaterThan(Double amount);
}
