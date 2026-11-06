package edu.marmara.cse3063.foodflow.application.dtos;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** The order details shown to the Customer for confirmation before the order is placed (UC6 step 3). */
public final class OrderPreviewDto implements IDto
{
    private final String restaurantName;
    private final List<OrderLineDto> lines;
    private final double subtotal;
    private final double deliveryFee;
    private final double total;
    private final String district;
    private final String street;
    private final String paymentMethod;

    public OrderPreviewDto(String restaurantName, List<OrderLineDto> lines, double subtotal, double deliveryFee,
                           double total, String district, String street, String paymentMethod)
    {
        this.restaurantName = Objects.requireNonNull(restaurantName, "restaurantName");
        this.lines = new ArrayList<>(Objects.requireNonNull(lines, "lines"));
        this.subtotal = subtotal;
        this.deliveryFee = deliveryFee;
        this.total = total;
        this.district = Objects.requireNonNull(district, "district");
        this.street = Objects.requireNonNull(street, "street");
        this.paymentMethod = Objects.requireNonNull(paymentMethod, "paymentMethod");
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

    public String getPaymentMethod()
    {
        return paymentMethod;
    }
}
