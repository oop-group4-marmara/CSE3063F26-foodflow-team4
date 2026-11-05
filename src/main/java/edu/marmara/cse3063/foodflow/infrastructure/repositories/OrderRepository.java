package edu.marmara.cse3063.foodflow.infrastructure.repositories;

import edu.marmara.cse3063.foodflow.domain.models.Order;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IOrderRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class OrderRepository implements IOrderRepository {
    private final Map<String, Order> store = new ConcurrentHashMap<>();

    @Override
    public List<Order> getOrders(String email) {
        Objects.requireNonNull(email, "email");

        return store.values().stream()
                .filter(order -> order.getCustomer().getEmail().equalsIgnoreCase(email))
                .toList();
    }
}
