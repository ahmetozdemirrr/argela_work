package com.argelaa.customerapi.service;

import com.argelaa.customerapi.model.Customer;
import com.argelaa.customerapi.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.argelaa.common.OrderPlacedEvent;

/* custom metric icin */
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService
{
    private final CustomerRepository customerRepository;

    /* custom metric icin */
    private final Counter ordersProcessedCounter;
    private final Counter newCustomersCounter;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, MeterRegistry meterRegistry)
    {
        this.customerRepository = customerRepository;

        /* Prometheus'ta görünecek metrikler */
        this.ordersProcessedCounter = Counter
                .builder("processed_orders_total")
                .description("Total number of orders processed from Kafka.")
                .register(meterRegistry);

        this.newCustomersCounter = Counter
                .builder("new_customers_total")
                .description("Total number of new customers created.")
                .register(meterRegistry);
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
        newCustomersCounter.increment();

        if (customer.getTotalProductsBought() == null) {
            customer.setTotalProductsBought(0L);
        }
        if (customer.getTotalSpentAmount() == null) {
            customer.setTotalSpentAmount(0.0);
        }
        return customerRepository.save(customer);
    }

    public void handleOrderPlaced(OrderPlacedEvent event)
    {
        ordersProcessedCounter.increment(); /* Sipariş işlendiğinde sayacı 1 artır */

        Customer customer = customerRepository.findById(event.getCustomerId()).orElseGet(() -> {
            return new Customer();
        });

        customer.setTotalProductsBought(customer.getTotalProductsBought() + event.getQuantityBought());
        customer.setTotalSpentAmount(customer.getTotalSpentAmount() + (event.getPricePerUnit() * event.getQuantityBought()));
        customerRepository.save(customer);
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

    @Transactional
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
