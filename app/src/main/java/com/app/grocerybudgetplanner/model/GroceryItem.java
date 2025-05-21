package com.app.grocerybudgetplanner.model;

import java.io.Serializable;

public class GroceryItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private double price;
    private int quantity;
    public GroceryItem() {}
    public GroceryItem(String id, String name, double price, int quantity) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        this.price = price;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
    }
    public double getTotalCost() {
        return Math.round(price * quantity * 1000.0) / 1000.0;  // Round to 3 decimal places
    }
    @Override
    public String toString() {
        return "GroceryItem{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", totalCost=" + getTotalCost() +
                '}';
    }
}
