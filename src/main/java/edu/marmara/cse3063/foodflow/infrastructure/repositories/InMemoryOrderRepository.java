package edu.marmara.cse3063.foodflow.infrastructure.repositories;

import edu.marmara.cse3063.foodflow.domain.models.Order;
import edu.marmara.cse3063.foodflow.application.repositories.IOrderRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;

public class InMemoryOrderRepository implements IOrderRepository
{
    private final AtomicLong lastIssuedNumber = new AtomicLong();
    private final Map<Long, Order> store = new ConcurrentSkipListMap<>();

    @Override
    public long nextOrderNumber()
    {
        return lastIssuedNumber.incrementAndGet();
    }

    @Override
    public Order saveOrder(Order order)
    {
        Objects.requireNonNull(order, "order");
        long number = order.getOrderNumber();
        if (number < 1 || number > lastIssuedNumber.get() || store.containsKey(number))
        {
            throw new IllegalStateException("Order number " + number + " was not issued by this repository.");
        }
        store.put(number, order);
        return order;
    }

    @Override
    public List<Order> findOrdersByCustomerEmail(String email)
    {
        Objects.requireNonNull(email, "email");
        String key = email.trim();
        return store.values().stream()
                .filter(order -> order.getCustomer().getEmail().equalsIgnoreCase(key))
                .toList();
    }
}
