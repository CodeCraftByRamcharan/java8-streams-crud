Overview

This microservice demonstrates the power of Java 8 Streams using a complex JSON dataset of customers, orders, and products.
It covers all major Stream operations, including:

Filtering: filter(), anyMatch(), allMatch(), noneMatch()
Mapping: map(), mapToDouble(), flatMap()
Sorting: sorted()
Reduction: reduce(), sum(), max()
Collecting: collect(), Collectors.groupingBy(), Collectors.toList(), Collectors.joining()
Parallel Streams: parallelStream() for performance testing
Matching & Validation: allMatch, anyMatch, noneMatch
Aggregation: Total revenue, spending per customer, top products
Advanced operations: Optional, Map.Entry, and LinkedHashMap usage for sorted results

This project is perfect for learning, showcasing, and experimenting with Java 8 Stream API features.

Features / Endpoints

Get all customer names
Concatenate all customer names into a single string
Retrieve orders for a customer by ID
Get the latest order per customer
Fetch top N most expensive products
Get frequently purchased products
Compute total revenue (sequential & parallel)
Find big spenders
Check customer data validation: emails, order existence
Find the most expensive product
All endpoints are fully documented with Swagger/OpenAPI, including request parameters and example responses.
