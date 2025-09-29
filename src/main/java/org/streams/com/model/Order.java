package org.streams.com.model;

import java.util.List;

public class Order {
    private int orderId;
    private String date;
    private List<Product> products;

    public Order() {}

    public Order(int orderId, String date, List<Product> products) {
        this.orderId = orderId;
        this.date = date;
        this.products = products;
    }

    // Getters & Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public List<Product> getProducts() { return products; }
    public void setProducts(List<Product> products) { this.products = products; }
}
