package org.streams.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.streams.com.model.Customer;
import org.streams.com.model.Order;
import org.streams.com.model.Product;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Customer-related operations.
 * Uses Java 8 Streams extensively for processing customers, orders, and products.
 */
@Service
public class CustomerService {

    @Autowired
    private JsonReaderService jsonReaderService;

    // --------------------------- Customer Info ---------------------------

    /**
     * Get all customer names.
     * Uses: map, collect
     */
    public List<String> getAllCustomerName() {
        List<Customer> customersList = jsonReaderService.readCustomers();
        return customersList.stream()
                .map(Customer::getName)
                .collect(Collectors.toList());
    }

    /**
     * Concatenate all customer names into a single string.
     * Uses: map, collect(joining)
     */
    public String concatenateCustomerNames() {
        List<Customer> customers = jsonReaderService.readCustomers();
        return customers.stream()
                .map(Customer::getName)
                .collect(Collectors.joining(", "));
    }

    // --------------------------- Orders ---------------------------

    /**
     * Get all orders for a specific customer by ID.
     * Uses: filter, flatMap, collect
     */
    public List<Order> getAllOrderForCustomerId(Integer custId) {
        List<Customer> customersList = jsonReaderService.readCustomers();
        return customersList.stream()
                .filter(c -> c.getId() == custId)
                .flatMap(c -> c.getOrders().stream())
                .collect(Collectors.toList());
    }

    /**
     * Get the latest order per customer.
     * Uses: collect(toMap), max
     */
    public Map<String, Order> getLatestOrderPerCustomer() {
        List<Customer> customersList = jsonReaderService.readCustomers();
        return customersList.stream()
                .collect(Collectors.toMap(
                        Customer::getName,
                        c -> c.getOrders().stream()
                                .max(Comparator.comparing(Order::getDate))
                                .orElse(null)
                ));
    }

    // --------------------------- Products ---------------------------

    /**
     * Get top N most expensive products across all customers.
     * Uses: flatMap, sorted, limit, collect
     */
    public List<Product> getTopExpensiveProducts(Integer top) {
        List<Customer> customersList = jsonReaderService.readCustomers();
        return customersList.stream()
                .flatMap(c -> c.getOrders().stream())
                .flatMap(o -> o.getProducts().stream())
                .sorted(Comparator.comparingDouble(Product::getPrice).reversed())
                .limit(top)
                .collect(Collectors.toList());
    }

    /**
     * Get top N most expensive products (overloaded with validation).
     * Uses: flatMap, sorted, limit, collect
     */
    public List<Product> getTopExpensiveProducts(int topN) {
        if (topN <= 0) {
            throw new IllegalArgumentException("topN must be greater than 0");
        }

        return jsonReaderService.readCustomers().stream()
                .flatMap(c -> c.getOrders().stream())
                .flatMap(o -> o.getProducts().stream())
                .sorted(Comparator.comparingDouble(Product::getPrice).reversed())
                .limit(topN)
                .collect(Collectors.toList());
    }

    /**
     * Find the single most expensive product.
     * Uses: flatMap, reduce
     */
    public Optional<Product> mostExpensiveProduct() {
        List<Customer> customers = jsonReaderService.readCustomers();
        return customers.stream()
                .flatMap(c -> c.getOrders().stream())
                .flatMap(o -> o.getProducts().stream())
                .reduce((p1, p2) -> p1.getPrice() > p2.getPrice() ? p1 : p2);
    }

    /**
     * Get the top frequently purchased products.
     * Uses: flatMap, collect(groupingBy, counting), sorted, limit
     */
    public List<Map.Entry<String, Long>> fetchFrequentlyPurchasedProduct(Integer count) {
        List<Customer> customersList = jsonReaderService.readCustomers();
        Map<String, Long> productFrequency = customersList.stream()
                .flatMap(c -> c.getOrders().stream())
                .flatMap(o -> o.getProducts().stream())
                .collect(Collectors.groupingBy(Product::getName, Collectors.counting()));

        return productFrequency.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    // --------------------------- Spending ---------------------------

    /**
     * Calculate total spending per customer.
     * Uses: collect(toMap), flatMap, mapToDouble, sum
     */
    public Map<String, Double> fetchSpendingPerCustomer() {
        List<Customer> customersList = jsonReaderService.readCustomers();
        return customersList.stream()
                .collect(Collectors.toMap(
                        Customer::getName,
                        c -> c.getOrders().stream()
                                .flatMap(o -> o.getProducts().stream())
                                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                                .sum()
                ));
    }

    /**
     * Fetch customers whose spending is greater than a given amount.
     * Uses: filter, flatMap, mapToDouble, sum, collect
     */
    public List<Customer> fetchCustomerSpendingGreaterThan(Integer amount) {
        List<Customer> customersList = jsonReaderService.readCustomers();
        return customersList.stream()
                .filter(c -> c.getOrders().stream()
                        .flatMap(o -> o.getProducts().stream())
                        .mapToDouble(p -> p.getPrice() * p.getQuantity())
                        .sum() > amount)
                .collect(Collectors.toList());
    }

    /**
     * Find customers who spent more than a threshold using parallel streams.
     * Uses: parallelStream, filter, flatMap, mapToDouble, sum, collect
     */
    public List<Customer> findBigSpenders(double threshold) {
        List<Customer> customers = jsonReaderService.readCustomers();
        return customers.parallelStream()
                .filter(c -> c.getOrders().stream()
                        .flatMap(o -> o.getProducts().stream())
                        .mapToDouble(p -> p.getPrice() * p.getQuantity())
                        .sum() > threshold)
                .collect(Collectors.toList());
    }

    // --------------------------- Validation Checks ---------------------------

    /**
     * Check if all customers have at least one order.
     * Uses: allMatch
     */
    public boolean allCustomersHaveOrders() {
        List<Customer> customers = jsonReaderService.readCustomers();
        return customers.stream()
                .allMatch(c -> c.getOrders() != null && !c.getOrders().isEmpty());
    }

    /**
     * Check if any customer spent over a specified amount.
     * Uses: anyMatch, flatMap, mapToDouble, sum
     */
    public boolean anyCustomerSpentOver(double amount) {
        List<Customer> customers = jsonReaderService.readCustomers();
        return customers.stream()
                .anyMatch(c -> c.getOrders().stream()
                        .flatMap(o -> o.getProducts().stream())
                        .mapToDouble(p -> p.getPrice() * p.getQuantity())
                        .sum() > amount);
    }

    /**
     * Check if no customer has empty email.
     * Uses: noneMatch
     */
    public boolean noCustomerHasEmptyEmail() {
        List<Customer> customers = jsonReaderService.readCustomers();
        return customers.stream()
                .noneMatch(c -> c.getEmail() == null || c.getEmail().trim().isEmpty());
    }

    // --------------------------- Revenue ---------------------------

    /**
     * Calculate total revenue across all orders.
     * Uses: flatMap, mapToDouble, sum
     */
    public double totalRevenue() {
        List<Customer> customers = jsonReaderService.readCustomers();
        return customers.stream()
                .flatMap(c -> c.getOrders().stream())
                .flatMap(o -> o.getProducts().stream())
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
    }

    /**
     * Calculate total revenue sequentially (same as totalRevenue).
     * Uses: sequential stream, flatMap, mapToDouble, sum
     */
    public double totalRevenueSequential() {
        List<Customer> customers = jsonReaderService.readCustomers();
        return customers.stream()
                .flatMap(c -> c.getOrders().stream())
                .flatMap(o -> o.getProducts().stream())
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
    }

    /**
     * Calculate total revenue using parallel streams.
     * Uses: parallelStream, flatMap, mapToDouble, sum
     */
    public double totalRevenueParallel() {
        List<Customer> customers = jsonReaderService.readCustomers();
        return customers.parallelStream()
                .flatMap(c -> c.getOrders().parallelStream())
                .flatMap(o -> o.getProducts().parallelStream())
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
    }
}
