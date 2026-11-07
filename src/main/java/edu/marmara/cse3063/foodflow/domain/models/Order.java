package edu.marmara.cse3063.foodflow.domain.models;

import java.util.ArrayList;
import java.util.Objects;

public class Order
{
    private final long orderNumber;
    private OrderStatus status = OrderStatus.CHECKOUT;
    private final PaymentMethod paymentMethod = PaymentMethod.CASH_ON_DELIVERY; //There is only cash in itaretion 1
    private final Customer customer;
    private final Restaurant restaurant;
    private final Address deliveryAddress;
    private final ArrayList<OrderLine> lines;
    private final double deliveryFee;
    private final double subtotal;
    private final double total;

    public Order(long orderNumber, Customer customer, Restaurant restaurant, Address deliveryAddress, ArrayList<OrderLine> lines)
    {
        this.orderNumber = orderNumber;
        this.customer = Objects.requireNonNull(customer, "customer");
        this.restaurant = Objects.requireNonNull(restaurant, "restaurant");
        this.deliveryAddress = Objects.requireNonNull(deliveryAddress, "deliveryAddress");
        this.lines = new ArrayList<>(Objects.requireNonNull(lines, "lines"));
        if (this.lines.isEmpty())
        {
            throw new IllegalStateException("An order must contain at least one order line.");
        }

        this.deliveryFee = restaurant.getDeliveryFee();
        double subtotal = 0.0;
        for (OrderLine line : this.lines)
        {
            subtotal += line.getLineTotal();
        }
        this.subtotal = subtotal;
        this.total = subtotal + deliveryFee;
    }

    public long getOrderNumber()
    {
        return orderNumber;
    }

    public OrderStatus getStatus()
    {
        return status;
    }

    public void setStatus(OrderStatus status)
    {
        this.status = Objects.requireNonNull(status, "status");
    }

    public PaymentMethod getPaymentMethod()
    {
        return paymentMethod;
    }

    public Customer getCustomer()
    {
        return customer;
    }

    public Restaurant getRestaurant()
    {
        return restaurant;
    }

    public Address getDeliveryAddress()
    {
        return deliveryAddress;
    }

    public ArrayList<OrderLine> getLines()
    {
        return new ArrayList<>(lines);
    }

    public double getDeliveryFee()
    {
        return deliveryFee;
    }

    public double getSubtotal()
    {
        return subtotal;
    }

    public double getTotal()
    {
        return total;
    }
}
