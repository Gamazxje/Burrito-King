package main.java.model;

public class FoodItem {
    private String name;
    private double price;
    private int quantity;

    public FoodItem(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return price * quantity;
    }

   
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
