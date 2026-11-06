package edu.marmara.cse3063.foodflow.application.dtos;

import java.util.ArrayList;
import java.util.Objects;

public final class CartDto implements IDto {
    private final String restaurantName;
    private final ArrayList<CartLineDto> lines;
    private final double subtotal;
    private final double deliveryFee;
    private final double total;

    /** restaurantName is null while the cart is empty. */
    public CartDto(String restaurantName, ArrayList<CartLineDto> lines, double subtotal, double deliveryFee, double total) {
        this.restaurantName = restaurantName;
        this.lines = new ArrayList<>(Objects.requireNonNull(lines, "lines"));
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.total = total;
    }

    public ArrayList<CartLineDto> getLines() {
        return new ArrayList<>(lines);
    }

    public String getRestaurantName() { return restaurantName; }
    public double getSubtotal() { return subtotal; }
    public double getDeliveryFee() { return deliveryFee; }
    public double getTotal() { return total; }
}