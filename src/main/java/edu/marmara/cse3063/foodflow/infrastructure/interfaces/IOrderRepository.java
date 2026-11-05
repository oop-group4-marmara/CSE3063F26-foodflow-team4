package edu.marmara.cse3063.foodflow.infrastructure.interfaces;

import edu.marmara.cse3063.foodflow.domain.models.Order;

import java.util.List;

public interface IOrderRepository {
    List<Order> getOrders(String Email);
}
