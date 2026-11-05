package edu.marmara.cse3063.foodflow.application.dtos;
import edu.marmara.cse3063.foodflow.domain.models.Order;
import edu.marmara.cse3063.foodflow.domain.models.OrderLine;
import edu.marmara.cse3063.foodflow.domain.models.OrderStatus;
import edu.marmara.cse3063.foodflow.domain.models.PaymentMethod;

import java.util.ArrayList;
import java.util.List;

public class OrderDto
{
    private final long orderNumber;
    private final OrderStatus status;
    private final PaymentMethod paymentMethod;
    private final String customerName;
    private final String customerEmail;
    private final String restaurantName;
    private final AddressDto deliveryAddress;
    private final List<OrderLineDto> lines;
    private final double deliveryFee;
    private final double subtotal;
    private final double total;

    public OrderDto(long orderNumber,
                    OrderStatus status,
                    PaymentMethod paymentMethod,
                    String customerName,
                    String customerEmail,
                    String restaurantName,
                    AddressDto deliveryAddress,
                    List<OrderLineDto> lines,
                    double deliveryFee,
                    double subtotal,
                    double total)
    {
        this.orderNumber = orderNumber;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.restaurantName = restaurantName;
        this.deliveryAddress = deliveryAddress;
        this.lines = new ArrayList<>(lines);
        this.deliveryFee = deliveryFee;
        this.subtotal = subtotal;
        this.total = total;
    }

    public static OrderDto from(Order order)
    {
        return new OrderDto(
                order.getOrderNumber(),
                order.getStatus(),
                order.getPaymentMethod(),
                order.getCustomer().getName(),
                order.getCustomer().getEmail(),
                order.getRestaurant().getName(),
                AddressDto.from(order.getDeliveryAddress()),
                order.getLines().stream().map(OrderLineDto::from).toList(),
                order.getDeliveryFee(),
                order.getSubtotal(),
                order.getTotal());
    }

    public long getOrderNumber()
    {
        return orderNumber;
    }

    public OrderStatus getStatus()
    {
        return status;
    }

    public PaymentMethod getPaymentMethod()
    {
        return paymentMethod;
    }

    public String getCustomerName()
    {
        return customerName;
    }

    public String getCustomerEmail()
    {
        return customerEmail;
    }

    public String getRestaurantName()
    {
        return restaurantName;
    }

    public AddressDto getDeliveryAddress()
    {
        return deliveryAddress;
    }

    public List<OrderLineDto> getLines()
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