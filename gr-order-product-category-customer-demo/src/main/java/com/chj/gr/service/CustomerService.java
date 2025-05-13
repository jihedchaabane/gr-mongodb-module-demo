package com.chj.gr.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.chj.gr.model.Customer;
import com.chj.gr.repository.CustomerRepository;

@Service
public class CustomerService {
    @Autowired
    private CustomerRepository customerRepository;

    public Customer createCustomer(Customer customer) {
        return customerRepository.save(customer);
    }

    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer updateCustomer(String id, Customer customer) {
        customer.setCustomerId(id);
        return customerRepository.save(customer);
    }

    public void deleteCustomer(String id) {
        customerRepository.deleteById(id);
    }

    public List<Customer> findCustomersWithOrders() {
        return customerRepository.findCustomersWithOrders();
    }

    public List<Customer> findByNamePartial(String name) {
        return customerRepository.findByNamePartial(name);
    }

    public List<Customer> findCustomersWithMinSpending(double minSpending) {
        return customerRepository.findCustomersWithMinSpending(minSpending);
    }

    public List<Customer> findTopActiveCustomers(int limit) {
        return customerRepository.findTopActiveCustomers(limit);
    }
}