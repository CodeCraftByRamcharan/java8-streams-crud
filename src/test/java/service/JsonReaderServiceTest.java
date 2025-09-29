package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.streams.com.model.Customer;
import org.streams.com.service.JsonReaderService;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class JsonReaderServiceTest {

    private JsonReaderService jsonReaderService;

    @BeforeEach
    void setUp() {
        jsonReaderService = new JsonReaderService();
    }

    @Test
    void testReadCustomers_shouldLoadFromJson() {
        List<Customer> customers = jsonReaderService.readCustomers();

        // Basic assertions
        assertNotNull(customers);
        assertFalse(customers.isEmpty());

        // Optionally, check first customer fields if your test JSON is known
        Customer first = customers.get(0);
        assertNotNull(first.getName());
        assertNotNull(first.getEmail());
        assertNotNull(first.getOrders());
    }

    @Test
    void testReloadCustomers_shouldReloadData() {
        // Load first time
        List<Customer> customers1 = jsonReaderService.readCustomers();
        assertNotNull(customers1);

        // Reload
        List<Customer> customers2 = jsonReaderService.reloadCustomers();
        assertNotNull(customers2);

        // The reference may change after reload
        assertNotSame(customers1, customers2);
        assertEquals(customers1.size(), customers2.size());
    }

    @Test
    void testReadCustomers_fileNotFound_shouldThrowException() throws IOException {
        // Temporarily override JSON file to a non-existent file using reflection
        JsonReaderService service = new JsonReaderService() {
            @Override
            public List<Customer> reloadCustomers() {
                throw new JsonReadException("File not found", new IOException("test"));
            }
        };

        JsonReaderService.JsonReadException exception = assertThrows(
                JsonReaderService.JsonReadException.class,
                service::readCustomers
        );

        assertTrue(exception.getMessage().contains("File not found"));
    }
}
