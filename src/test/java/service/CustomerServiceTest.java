package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.streams.com.JavaStreamsCrudApplication;
import org.streams.com.model.Customer;
import org.streams.com.model.Order;
import org.streams.com.model.Product;
import org.streams.com.service.CustomerService;
import org.streams.com.service.JsonReaderService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private JsonReaderService jsonReaderService;

    @InjectMocks
    private CustomerService customerService;

    private Customer customer1;
    private Customer customer2;
    private Order order1;
    private Order order2;
    private Product product1;
    private Product product2;
    private List<Customer> customers;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Create products
        product1 = new Product(1, "Laptop", 1000.0, 2);
        product2 = new Product(2, "Phone", 500.0, 3);

        // Create orders
        order1 = new Order(1, "2025-09-29", List.of(product1, product2));
        order2 = new Order(2, "2025-09-30", List.of(product2));

        // Create customers
        customer1 = new Customer(1, "Alice", "alice@example.com", List.of(order1));
        customer2 = new Customer(2, "Bob", "bob@example.com", List.of(order2));

        customers = List.of(customer1, customer2);

        // Mock JsonReaderService
        when(jsonReaderService.readCustomers()).thenReturn(customers);
    }

    // -------------------- Customer Info --------------------
    @Test
    void testGetAllCustomerName() {
        List<String> names = customerService.getAllCustomerName();
        assertEquals(List.of("Alice", "Bob"), names);
    }

    @Test
    void testConcatenateCustomerNames() {
        String concatenated = customerService.concatenateCustomerNames();
        assertEquals("Alice, Bob", concatenated);
    }

    // -------------------- Orders --------------------
    @Test
    void testGetAllOrderForCustomerId() {
        List<Order> orders = customerService.getAllOrderForCustomerId(1);
        assertEquals(1, orders.size());
        assertEquals(order1, orders.get(0));
    }

    @Test
    void testGetLatestOrderPerCustomer() {
        Map<String, Order> latestOrders = customerService.getLatestOrderPerCustomer();
        assertEquals(2, latestOrders.size());
        assertEquals(order1, latestOrders.get("Alice"));
        assertEquals(order2, latestOrders.get("Bob"));
    }

    // -------------------- Products --------------------
    @Test
    void testGetTopExpensiveProducts() {
        List<Product> topProducts = customerService.getTopExpensiveProducts(2);
        assertEquals(2, topProducts.size());
        assertEquals(product1, topProducts.get(0));
    }

    @Test
    void testMostExpensiveProduct() {
        Optional<Product> mostExpensive = customerService.mostExpensiveProduct();
        assertTrue(mostExpensive.isPresent());
        assertEquals(product1, mostExpensive.get());
    }

    @Test
    void testFetchFrequentlyPurchasedProduct() {
        List<Map.Entry<String, Long>> freqProducts = customerService.fetchFrequentlyPurchasedProduct(2);
        assertEquals(2, freqProducts.size());
        assertEquals("Phone", freqProducts.get(0).getKey());
        assertEquals(2L, freqProducts.get(0).getValue());
    }

    // -------------------- Spending --------------------
    @Test
    void testFetchSpendingPerCustomer() {
        Map<String, Double> spending = customerService.fetchSpendingPerCustomer();
        assertEquals(2, spending.size());
        assertEquals(1000.0 * 2 + 500.0 * 3, spending.get("Alice"));
    }

    @Test
    void testFetchCustomerSpendingGreaterThan() {
        List<Customer> bigSpenders = customerService.fetchCustomerSpendingGreaterThan(1500);
        assertEquals(1, bigSpenders.size());
        assertEquals(customer1, bigSpenders.get(0));
    }

    @Test
    void testFindBigSpenders() {
        List<Customer> bigSpenders = customerService.findBigSpenders(1500);
        assertEquals(1, bigSpenders.size());
        assertEquals(customer1, bigSpenders.get(0));
    }

    // -------------------- Validation --------------------
    @Test
    void testAllCustomersHaveOrders() {
        assertTrue(customerService.allCustomersHaveOrders());
    }

    @Test
    void testAnyCustomerSpentOver() {
        assertTrue(customerService.anyCustomerSpentOver(1000));
        assertFalse(customerService.anyCustomerSpentOver(5000));
    }

    @Test
    void testNoCustomerHasEmptyEmail() {
        assertTrue(customerService.noCustomerHasEmptyEmail());
    }

    // -------------------- Revenue --------------------
    @Test
    void testTotalRevenue() {
        double expected = (1000.0 * 2 + 500.0 * 3) + (500.0 * 3);
        assertEquals(expected, customerService.totalRevenue());
        assertEquals(expected, customerService.totalRevenueSequential());
        assertEquals(expected, customerService.totalRevenueParallel());
    }
}
