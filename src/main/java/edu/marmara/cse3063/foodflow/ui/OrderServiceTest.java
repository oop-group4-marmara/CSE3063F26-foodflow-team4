package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.interfaces.ICustomerService;
import edu.marmara.cse3063.foodflow.application.services.OrderService;
import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IOrderRepository;

public class OrderServiceTest {
        private static final String EMAIL = "ali@mail.com";

        private IOrderRepository orderRepository;
        private ICustomerService customerService;
        private OrderService orderService;

        private Customer customer;
        private Restaurant restaurant;
        private Address address;
}
