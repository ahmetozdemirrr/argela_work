package com.argelaa.customerapi.service;

import com.argelaa.common.OrderPlacedEvent;
import com.argelaa.customerapi.metric.KPIMetricsService;
import com.argelaa.customerapi.model.Customer;
import com.argelaa.customerapi.repository.CustomerRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final MeterRegistry meterRegistry;
    private final Counter newCustomersCounter;
    private final KPIMetricsService kpiMetricsService; // Keep this for other KPIs

    @Autowired
    public CustomerService(CustomerRepository customerRepository, MeterRegistry meterRegistry, KPIMetricsService kpiMetricsService) {
        this.customerRepository = customerRepository;
        this.meterRegistry = meterRegistry;
        this.kpiMetricsService = kpiMetricsService; // Enjekte et

        // This counter is not labeled, so it's fine to initialize it here.
        this.newCustomersCounter = Counter
                .builder("new_customers_total")
                .description("Total number of new customers created.")
                .register(meterRegistry);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public Customer addCustomer(Customer customer) {
        newCustomersCounter.increment();

        if (customer.getTotalProductsBought() == null) {
            customer.setTotalProductsBought(0L);
        }
        if (customer.getTotalSpentAmount() == null) {
            customer.setTotalSpentAmount(0.0);
        }
        return customerRepository.save(customer);
    }

    public void handleOrderPlaced(OrderPlacedEvent event) {
        // Dynamically create/increment the counter with the product_category label
        meterRegistry.counter("processed_orders_total", "product_category", event.getProductCategory()).increment();

        Customer customer = customerRepository.findById(event.getCustomerId()).orElseGet(Customer::new);

        customer.setTotalProductsBought(customer.getTotalProductsBought() + event.getQuantityBought());
        customer.setTotalSpentAmount(customer.getTotalSpentAmount() + (event.getPricePerUnit() * event.getQuantityBought()));
        customerRepository.save(customer);
    }

    public Optional<Customer> updateCustomer(Long id, Customer updatedCustomer) {
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

    public boolean deleteCustomer(Long id) {
        if (customerRepository.existsById(id)) {
            customerRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Transactional
    public Optional<Customer> recordPurchase(Long customerId, Long quantityBought, Double pricePerUnit) {
        return customerRepository.findById(customerId).map(customer -> {
            customer.setTotalProductsBought(customer.getTotalProductsBought() + quantityBought);
            customer.setTotalSpentAmount(customer.getTotalSpentAmount() + (quantityBought * pricePerUnit));
            return customerRepository.save(customer);
        });
    }

    public Optional<Customer> getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public List<Customer> getCustomersWhoSpentMoreThan(Double amount) {
        return customerRepository.findByTotalSpentAmountGreaterThan(amount);
    }
}