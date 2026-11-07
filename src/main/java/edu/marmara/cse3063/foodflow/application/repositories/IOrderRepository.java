package edu.marmara.cse3063.foodflow.application.repositories;

import edu.marmara.cse3063.foodflow.domain.models.Order;

import java.util.List;

public interface IOrderRepository
{
    /** Returns a number greater than every order number already issued in this run (BR-6). */
    long nextOrderNumber();

    Order saveOrder(Order order);

    /** Returns the customer's orders in ascending order number. */
    List<Order> findOrdersByCustomerEmail(String email);
}
