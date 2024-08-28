package main.java.model;

import java.time.LocalDateTime;

public class Order {
    private String orderNumber;
    private String username;
    private LocalDateTime orderTime;
    private double totalPrice;
    private String status;
    private LocalDateTime collectionTime;

    public Order(String orderNumber, LocalDateTime orderTime) {
        this.orderNumber = orderNumber;
        this.orderTime = orderTime;
    }

    public Order(String orderNumber, String username, LocalDateTime orderTime, double totalPrice, String status) {
        this.orderNumber = orderNumber;
        this.username = username;
        this.orderTime = orderTime;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCollectionTime() {
        return collectionTime;
    }

    public void setCollectionTime(LocalDateTime collectionTime) {
        this.collectionTime = collectionTime;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderNumber='" + orderNumber + '\'' +
                ", username='" + username + '\'' +
                ", orderTime=" + orderTime +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", collectionTime=" + collectionTime +
                '}';
    }
}
