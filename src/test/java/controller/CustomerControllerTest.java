package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ContextConfiguration;
import org.streams.com.JavaStreamsCrudApplication;
import org.streams.com.controller.CustomerController;
import org.streams.com.model.Customer;
import org.streams.com.model.Order;
import org.streams.com.model.Product;
import org.streams.com.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;

import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@ContextConfiguration(classes = JavaStreamsCrudApplication.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CustomerService customerService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<String> customerNames;
    private List<Customer> customers;
    private List<Order> orders;
    private List<Product> products;

    @BeforeEach
    void setUp() {
        customerNames = Arrays.asList("Alice", "Bob", "Charlie");

        Customer customer1 = new Customer(1, "Alice", "alice@example.com", new ArrayList<>());
        Customer customer2 = new Customer(2, "Bob", "bob@example.com", new ArrayList<>());
        customers = Arrays.asList(customer1, customer2);

        Order order1 = new Order(1, "Order1", new ArrayList<>());
        Order order2 = new Order(2, "Order2", new ArrayList<>());
        orders = Arrays.asList(order1, order2);

        Product product1 = new Product(1, "Laptop", 1000.0, 5);
        Product product2 = new Product(2, "Phone", 500.0, 10);
        products = Arrays.asList(product1, product2);
    }

    @Test
    void testGetCustomerNames() throws Exception {
        when(customerService.getAllCustomerName()).thenReturn(customerNames);

        mockMvc.perform(get("/api/customers/customer-name"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(customerNames)));
    }

    @Test
    void testConcatenateCustomerNames() throws Exception {
        when(customerService.concatenateCustomerNames()).thenReturn("Alice, Bob, Charlie");

        mockMvc.perform(get("/api/customers/customer-names"))
                .andExpect(status().isOk())
                .andExpect(content().string("Alice, Bob, Charlie"));
    }

    @Test
    void testGetOrdersByCustomerId() throws Exception {
        when(customerService.getAllOrderForCustomerId(anyInt())).thenReturn(orders);

        mockMvc.perform(get("/api/customers/1/orders"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(orders)));
    }

    @Test
    void testGetLatestOrderPerCustomer() throws Exception {
        Map<String, Order> latestOrders = new HashMap<>();
        latestOrders.put("Alice", orders.get(0));
        latestOrders.put("Bob", orders.get(1));

        when(customerService.getLatestOrderPerCustomer()).thenReturn(latestOrders);

        mockMvc.perform(get("/api/customers/latest-order-per-customer"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(latestOrders)));
    }

    @Test
    void testGetSpendingPerCustomer() throws Exception {
        Map<String, Double> spending = new HashMap<>();
        spending.put("Alice", 150.0);
        spending.put("Bob", 200.0);

        when(customerService.fetchSpendingPerCustomer()).thenReturn(spending);

        mockMvc.perform(get("/api/customers/spending-per-customer"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(spending)));
    }

    @Test
    void testGetCustomerSpendingGreaterThan() throws Exception {
        when(customerService.fetchCustomerSpendingGreaterThan(anyInt())).thenReturn(customers);

        mockMvc.perform(get("/api/customers/spent/greaterThan/100"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(customers)));
    }

    @Test
    void testFindBigSpenders() throws Exception {
        when(customerService.findBigSpenders(anyDouble())).thenReturn(customers);

        mockMvc.perform(get("/api/customers/big-spenders/1000"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(customers)));
    }

    @Test
    void testGetTopExpensiveProducts() throws Exception {
        when(customerService.getTopExpensiveProducts(anyInt())).thenReturn(products);

        mockMvc.perform(get("/api/customers/2/expensive/products"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(products)));
    }

    @Test
    void testMostExpensiveProduct() throws Exception {
        when(customerService.mostExpensiveProduct()).thenReturn(Optional.of(products.get(0)));

        mockMvc.perform(get("/api/customers/most-expensive-product"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(Optional.of(products.get(0)))));
    }

    @Test
    void testAllCustomersHaveOrders() throws Exception {
        when(customerService.allCustomersHaveOrders()).thenReturn(true);

        mockMvc.perform(get("/api/customers/all-have-orders"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testAnyCustomerSpentOver() throws Exception {
        when(customerService.anyCustomerSpentOver(anyDouble())).thenReturn(true);

        mockMvc.perform(get("/api/customers/any-spent-over/100"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testNoCustomerHasEmptyEmail() throws Exception {
        when(customerService.noCustomerHasEmptyEmail()).thenReturn(true);

        mockMvc.perform(get("/api/customers/no-empty-email"))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void testTotalRevenue() throws Exception {
        when(customerService.totalRevenue()).thenReturn(1000.0);

        mockMvc.perform(get("/api/customers/total-revenue"))
                .andExpect(status().isOk())
                .andExpect(content().string("1000.0"));
    }
}