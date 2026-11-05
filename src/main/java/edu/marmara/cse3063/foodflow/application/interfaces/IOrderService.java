package edu.marmara.cse3063.foodflow.application.interfaces;

import edu.marmara.cse3063.foodflow.application.dtos.OrderDto;

import java.util.List;

public interface IOrderService {
    public List<OrderDto> getCurrentCustomerOrders();
}
