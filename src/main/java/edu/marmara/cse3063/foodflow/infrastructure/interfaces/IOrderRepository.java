package edu.marmara.cse3063.foodflow.infrastructure.interfaces;

import edu.marmara.cse3063.foodflow.domain.models.Order;

public interface IOrderRepository
{
    /** Returns a number greater than every order number already issued in this run (BR-6). */
    long nextOrderNumber();

    Order saveOrder(Order order);
}
