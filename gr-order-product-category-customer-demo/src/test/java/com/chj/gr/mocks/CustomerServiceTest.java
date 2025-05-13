package com.chj.gr.mocks;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.chj.gr.model.Customer;
import com.chj.gr.repository.CustomerRepository;
import com.chj.gr.service.CustomerService;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_ShouldReturnSavedCustomer() {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.createCustomer(customer);

        assertNotNull(result);
        assertEquals("cust1", result.getCustomerId());
        assertEquals("Jean", result.getFirstName());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void getCustomerById_ShouldReturnCustomer_WhenIdExists() {
        Customer customer = new Customer("cust1", "Jean", "Dupont");
        when(customerRepository.findById("cust1")).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.getCustomerById("cust1");

        assertTrue(result.isPresent());
        assertEquals("cust1", result.get().getCustomerId());
        verify(customerRepository, times(1)).findById("cust1");
    }

    @Test
    void getCustomerById_ShouldReturnEmpty_WhenIdDoesNotExist() {
        when(customerRepository.findById("cust1")).thenReturn(Optional.empty());

        Optional<Customer> result = customerService.getCustomerById("cust1");

        assertFalse(result.isPresent());
        verify(customerRepository, times(1)).findById("cust1");
    }

    @Test
    void getAllCustomers_ShouldReturnListOfCustomers() {
        List<Customer> customers = Arrays.asList(
            new Customer("cust1", "Jean", "Dupont"),
            new Customer("cust2", "Marie", "Curie")
        );
        when(customerRepository.findAll()).thenReturn(customers);

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("Jean", result.get(0).getFirstName());
        verify(customerRepository, times(1)).findAll();
    }

    @Test
    void updateCustomer_ShouldReturnUpdatedCustomer() {
        Customer customer = new Customer("cust1", "Jean", "Dupont Updated");
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        Customer result = customerService.updateCustomer("cust1", customer);

        assertNotNull(result);
        assertEquals("cust1", result.getCustomerId());
        assertEquals("Dupont Updated", result.getLastName());
        verify(customerRepository, times(1)).save(customer);
    }

    @Test
    void deleteCustomer_ShouldCallDeleteById() {
        doNothing().when(customerRepository).deleteById("cust1");

        customerService.deleteCustomer("cust1");

        verify(customerRepository, times(1)).deleteById("cust1");
    }

    @Test
    void findCustomersWithOrders_ShouldReturnCustomersWithOrders() {
        List<Customer> customers = Arrays.asList(new Customer("cust1", "Jean", "Dupont"));
        when(customerRepository.findCustomersWithOrders()).thenReturn(customers);

        List<Customer> result = customerService.findCustomersWithOrders();

        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findCustomersWithOrders();
    }

    @Test
    void findByNamePartial_ShouldReturnMatchingCustomers() {
        List<Customer> customers = Arrays.asList(new Customer("cust1", "Jean", "Dupont"));
        when(customerRepository.findByNamePartial("jean")).thenReturn(customers);

        List<Customer> result = customerService.findByNamePartial("jean");

        assertEquals(1, result.size());
        assertEquals("Jean", result.get(0).getFirstName());
        verify(customerRepository, times(1)).findByNamePartial("jean");
    }

    @Test
    void findCustomersWithMinSpending_ShouldReturnMatchingCustomers() {
        List<Customer> customers = Arrays.asList(new Customer("cust1", "Jean", "Dupont"));
        when(customerRepository.findCustomersWithMinSpending(1000.0)).thenReturn(customers);

        List<Customer> result = customerService.findCustomersWithMinSpending(1000.0);

        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findCustomersWithMinSpending(1000.0);
    }

    @Test
    void findTopActiveCustomers_ShouldReturnTopCustomers() {
        List<Customer> customers = Arrays.asList(new Customer("cust1", "Jean", "Dupont"));
        when(customerRepository.findTopActiveCustomers(5)).thenReturn(customers);

        List<Customer> result = customerService.findTopActiveCustomers(5);

        assertEquals(1, result.size());
        verify(customerRepository, times(1)).findTopActiveCustomers(5);
    }
}