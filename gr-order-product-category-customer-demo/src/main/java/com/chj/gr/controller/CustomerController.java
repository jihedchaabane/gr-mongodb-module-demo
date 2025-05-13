package com.chj.gr.controller;

import com.chj.gr.model.Customer;
import com.chj.gr.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @PostMapping
    public Customer createCustomer(@Valid @RequestBody Customer customer) {
        return customerService.createCustomer(customer);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable String id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        return customerService.getAllCustomers();
    }

    @PutMapping("/{id}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable String id, @Valid @RequestBody Customer customer) {
        Customer updatedCustomer = customerService.updateCustomer(id, customer);
        return ResponseEntity.ok(updatedCustomer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/with-orders")
    public ResponseEntity<List<Customer>> findCustomersWithOrders() {
        List<Customer> customers = customerService.findCustomersWithOrders();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/search")
    public List<Customer> findByNamePartial(@RequestParam String name) {
        return customerService.findByNamePartial(name);
    }

    @GetMapping("/min-spending")
    public List<Customer> findCustomersWithMinSpending(@RequestParam double minSpending) {
        return customerService.findCustomersWithMinSpending(minSpending);
    }

    @GetMapping("/top-active")
    public List<Customer> findTopActiveCustomers(@RequestParam int limit) {
        return customerService.findTopActiveCustomers(limit);
    }
}