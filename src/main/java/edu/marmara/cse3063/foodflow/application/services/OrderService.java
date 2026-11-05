package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.dtos.OrderDto;
import edu.marmara.cse3063.foodflow.application.interfaces.ICustomerService;
import edu.marmara.cse3063.foodflow.application.interfaces.IOrderService;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.domain.models.Order;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IOrderRepository;

import java.util.List;
import java.util.Optional;

public class OrderService implements IOrderService {
    private final IOrderRepository orderRepository;
    private final ICustomerService customerService;

    public OrderService(IOrderRepository orderRepository, ICustomerService customerService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
    }

    @Override
    public List<OrderDto> getCurrentCustomerOrders() {
        Optional<Customer> currentCustomer = customerService.getActiveCustomer();

        if (currentCustomer.isEmpty()){
            throw new IllegalArgumentException("Current customer is null");
        }

        String email = currentCustomer.get().getEmail();

        List<Order> customerOrders = orderRepository.getOrders(email);

        List<OrderDto> orderDtoList = List.of();

        for (Order customerOrder : customerOrders) {
            orderDtoList.add(OrderDto.from(customerOrder));
        }

        return orderDtoList;
    }
}
