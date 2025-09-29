package org.streams.com.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.streams.com.model.Customer;

import java.io.IOException;
import java.util.List;

/**
 * Service to read customer data from a JSON file in the resources folder.
 * Supports caching for improved performance and reload for fresh data.
 */
@Service
public class JsonReaderService {

    private final ObjectMapper mapper = new ObjectMapper();

    // Cached list of customers to avoid reading file repeatedly
    private List<Customer> cachedCustomers;

    /**
     * Reads customers from JSON file.
     * Uses cached data if available.
     *
     * @return List of customers
     * @throws JsonReadException if the file cannot be read or parsed
     */
    public List<Customer> readCustomers() {
        if (cachedCustomers == null) {
            reloadCustomers();  // Load fresh data if cache is empty
        }
        return cachedCustomers;
    }

    /**
     * Reloads the customers from the JSON file, bypassing the cache.
     *
     * @return List of customers
     * @throws JsonReadException if the file cannot be read or parsed
     */
    public List<Customer> reloadCustomers() {
        try {
            // Load JSON file from classpath
            ClassPathResource resource = new ClassPathResource("CustomerDetails.json");

            // Read root JSON node
            JsonNode root = mapper.readTree(resource.getFile());

            // Convert "customers" node to List<Customer>
            cachedCustomers = mapper.convertValue(
                    root.get("customers"),
                    new TypeReference<List<Customer>>() {}
            );

            return cachedCustomers;

        } catch (IOException e) {
            // Wrap IOException in a custom unchecked exception
            throw new JsonReadException("Failed to read customers from data.json", e);
        }
    }

    /**
     * Custom runtime exception for JSON read errors.
     */
    public static class JsonReadException extends RuntimeException {
        public JsonReadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}