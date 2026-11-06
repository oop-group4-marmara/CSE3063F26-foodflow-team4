package edu.marmara.cse3063.foodflow.application.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** An immutable receipt of a placed order (UC6 step 5). */
public final class OrderDto implements IDto
{
    private final long orderNumber;
    private final String status;
    private final String paymentMethod;
    private final String restaurantName;
    private final List<OrderLineDto> lines;
    private final double subtotal;
    private final double deliveryFee;
    private final double total;
    private final String district;
    private final String street;

    public OrderDto(long orderNumber, String status, String paymentMethod, String restaurantName,
                    List<OrderLineDto> lines, double subtotal, double deliveryFee, double total,
                    String district, String street)
    {
        this.orderNumber = orderNumber;
        this.status = Objects.requireNonNull(status, "status");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "paymentMethod");
        this.restaurantName = Objects.requireNonNull(restaurantName, "restaurantName");
        this.lines = new ArrayList<>(Objects.requireNonNull(lines, "lines"));
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.total = total;
        this.district = Objects.requireNonNull(district, "district");
        this.street = Objects.requireNonNull(street, "street");
    }

    public long getOrderNumber()
    {
        return orderNumber;
    }

    public String getStatus()
    {
        return status;
    }

    public String getPaymentMethod()
    {
        return paymentMethod;
    }

    public String getRestaurantName()
    {
        return restaurantName;
    }

    public List<OrderLineDto> getLines()
    {
        return new ArrayList<>(lines);
    }

    public double getSubtotal()
    {
        return subtotal;
    }

    public double getDeliveryFee()
    {
        return deliveryFee;
    }

    public double getTotal()
    {
        return total;
    }

    public String getDistrict()
    {
        return district;
    }

    public String getStreet()
    {
        return street;
    }
}
