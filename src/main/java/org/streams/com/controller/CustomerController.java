package org.streams.com.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.streams.com.model.Customer;
import org.streams.com.model.Order;
import org.streams.com.model.Product;
import org.streams.com.service.CustomerService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * REST controller to manage Customer-related operations.
 * Provides endpoints to fetch customer information, orders, spending, and products using Java Streams.
 */
@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Controller", description = "APIs for managing customers, orders, and products")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // --------------------------- Customer Info ---------------------------

    @Operation(summary = "Get all customer names", description = "Fetches the list of all customer names.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customer names"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/customer-name")
    public ResponseEntity<List<String>> getCustomerNames() {
        return ResponseEntity.ok(customerService.getAllCustomerName());
    }

    @Operation(summary = "Concatenate all customer names", description = "Returns a single string with all customer names concatenated.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully concatenated customer names"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/customer-names")
    public ResponseEntity<String> concatenateCustomerNames() {
        return ResponseEntity.ok(customerService.concatenateCustomerNames());
    }

    // --------------------------- Orders ---------------------------

    @Operation(summary = "Get orders by customer ID", description = "Fetches all orders for a specific customer by ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved orders"),
            @ApiResponse(responseCode = "404", description = "Customer not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/orders")
    public ResponseEntity<List<Order>> getOrdersByCustomerId(@PathVariable int id) {
        List<Order> ordersList = customerService.getAllOrderForCustomerId(id);
        return ResponseEntity.ok(ordersList);
    }

    @Operation(summary = "Get latest order per customer", description = "Returns the most recent order for each customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved latest orders"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/latest-order-per-customer")
    public ResponseEntity<Map<String, Order>> getLatestOrderPerCustomer() {
        return ResponseEntity.ok(customerService.getLatestOrderPerCustomer());
    }

    // --------------------------- Spending ---------------------------

    @Operation(summary = "Get spending per customer", description = "Fetches the total spending of each customer.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved spending per customer"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/spending-per-customer")
    public ResponseEntity<Map<String, Double>> getSpendingPerCustomer() {
        Map<String, Double> spending = customerService.fetchSpendingPerCustomer();
        return ResponseEntity.ok(spending);
    }

    @Operation(summary = "Get customers spending greater than amount", description = "Returns customers whose spending exceeds a given amount.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved customers"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/spent/greaterThan/{amount}")
    public ResponseEntity<List<Customer>> getCustomerSpendingGreaterThan(@PathVariable Integer amount) {
        return ResponseEntity.ok(customerService.fetchCustomerSpendingGreaterThan(amount));
    }

    @Operation(summary = "Find big spenders", description = "Finds customers who spent more than the specified threshold.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved big spenders"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/big-spenders/{threshold}")
    public ResponseEntity<List<Customer>> findBigSpenders(@PathVariable double threshold) {
        return ResponseEntity.ok(customerService.findBigSpenders(threshold));
    }

    // --------------------------- Product Info ---------------------------

    @Operation(summary = "Get top expensive products", description = "Returns the top N most expensive products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved top products"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{top}/expensive/products")
    public ResponseEntity<List<Product>> getTopExpensiveProducts(@PathVariable int top) {
        List<Product> topExpensiveProduct = customerService.getTopExpensiveProducts(top);
        return ResponseEntity.ok(topExpensiveProduct);
    }

    @Operation(summary = "Get top expensive products with parallel streams", description = "Returns top N expensive products calculated using parallel streams.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved top products"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/top-products")
    public ResponseEntity<List<Product>> getTopExpensiveProductsWithParallelStreams(@RequestParam(defaultValue = "10") int topN) {
        return ResponseEntity.ok(customerService.getTopExpensiveProducts(topN));
    }

    @Operation(summary = "Get most expensive product", description = "Returns the single most expensive product.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved most expensive product"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/most-expensive-product")
    public ResponseEntity<Optional<Product>> mostExpensiveProduct() {
        return ResponseEntity.ok(customerService.mostExpensiveProduct());
    }

    @Operation(summary = "Get top frequently purchased products", description = "Returns the top N most frequently purchased products.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved frequently purchased products"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{top}/frequently-purchase-product")
    public ResponseEntity<List<Map.Entry<String, Long>>> getTopFrequentlyPurchasedProduct(@PathVariable Integer top) {
        return ResponseEntity.ok(customerService.fetchFrequentlyPurchasedProduct(top));
    }

    // --------------------------- Validation Checks ---------------------------

    @Operation(summary = "Check if all customers have orders", description = "Returns true if every customer has placed at least one order.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully checked all customers"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all-have-orders")
    public ResponseEntity<Boolean> allCustomersHaveOrders() {
        return ResponseEntity.ok(customerService.allCustomersHaveOrders());
    }

    @Operation(summary = "Check if any customer spent over amount", description = "Returns true if any customer has spent more than the specified amount.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully checked customer spending"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/any-spent-over/{amount}")
    public ResponseEntity<Boolean> anyCustomerSpentOver(@PathVariable double amount) {
        return ResponseEntity.ok(customerService.anyCustomerSpentOver(amount));
    }

    @Operation(summary = "Check if no customer has empty email", description = "Returns true if all customers have a valid email.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully validated customer emails"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/no-empty-email")
    public ResponseEntity<Boolean> noCustomerHasEmptyEmail() {
        return ResponseEntity.ok(customerService.noCustomerHasEmptyEmail());
    }

    // --------------------------- Revenue Calculations ---------------------------

    @Operation(summary = "Calculate total revenue", description = "Calculates the total revenue from all orders.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated total revenue"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/total-revenue")
    public ResponseEntity<Double> totalRevenue() {
        return ResponseEntity.ok(customerService.totalRevenue());
    }

    @Operation(summary = "Calculate total revenue sequentially", description = "Calculates total revenue using sequential streams.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated sequential revenue"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/total-revenue-sequential")
    public ResponseEntity<Double> totalRevenueSequential() {
        return ResponseEntity.ok(customerService.totalRevenueSequential());
    }

    @Operation(summary = "Calculate total revenue in parallel", description = "Calculates total revenue using parallel streams.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully calculated parallel revenue"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/total-revenue-parallel")
    public ResponseEntity<Double> totalRevenueParallel() {
        return ResponseEntity.ok(customerService.totalRevenueParallel());
    }
}
