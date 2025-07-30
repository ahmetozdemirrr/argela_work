package com.argelaa.customerapi.service;

import com.argelaa.customerapi.model.Customer;
import com.argelaa.customerapi.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService
{
    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository)
    {
        this.customerRepository = customerRepository;
    }

    public List<Customer> getAllCustomers()
    {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id)
    {
        return customerRepository.findById(id);
    }

    public Customer addCustomer(Customer customer)
    {
        if (customer.getTotalProductsBought() == null) {
            customer.setTotalProductsBought(0L);
        }
        if (customer.getTotalSpentAmount() == null) {
            customer.setTotalSpentAmount(0.0);
        }
        return customerRepository.save(customer);
    }

    public Optional<Customer> updateCustomer(Long id, Customer updatedCustomer)
    {
        return customerRepository.findById(id).map(existingCustomer -> {
            existingCustomer.setFirstName(updatedCustomer.getFirstName());
            existingCustomer.setLastName(updatedCustomer.getLastName());
            existingCustomer.setEmail(updatedCustomer.getEmail());
            if (updatedCustomer.getTotalProductsBought() != null) {
                existingCustomer.setTotalProductsBought(updatedCustomer.getTotalProductsBought());
            }
            if (updatedCustomer.getTotalSpentAmount() != null) {
                existingCustomer.setTotalSpentAmount(updatedCustomer.getTotalSpentAmount());
            }
            return customerRepository.save(existingCustomer);
        });
    }

    public boolean deleteCustomer(Long id)
    {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Customer> recordPurchase(Long customerId, Long quantityBought, Double pricePerUnit)
    {
        return customerRepository.findById(customerId).map(customer -> {
            customer.setTotalProductsBought(customer.getTotalProductsBought() + quantityBought);
            customer.setTotalSpentAmount(customer.getTotalSpentAmount() + (quantityBought * pricePerUnit));
            return customerRepository.save(customer);
        });
    }

    public Optional<Customer> getCustomerByEmail(String email)
    {
        return customerRepository.findByEmail(email);
    }

    public List<Customer> getCustomersWhoSpentMoreThan(Double amount)
    {
        return customerRepository.findByTotalSpentAmountGreaterThan(amount);
    }
}
