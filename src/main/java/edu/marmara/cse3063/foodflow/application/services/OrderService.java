package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.dtos.OrderDto;
import edu.marmara.cse3063.foodflow.application.dtos.OrderLineDto;
import edu.marmara.cse3063.foodflow.application.dtos.OrderPreviewDto;
import edu.marmara.cse3063.foodflow.application.interfaces.IOrderService;
import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Cart;
import edu.marmara.cse3063.foodflow.domain.models.CartLine;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.domain.models.Order;
import edu.marmara.cse3063.foodflow.domain.models.OrderLine;
import edu.marmara.cse3063.foodflow.domain.models.OrderStatus;
import edu.marmara.cse3063.foodflow.domain.models.PaymentMethod;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.ICustomerRepository;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IOrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class OrderService implements IOrderService
{
    private final ICustomerRepository customerRepository;
    private final IOrderRepository orderRepository;

    public OrderService(ICustomerRepository customerRepository, IOrderRepository orderRepository)
    {
        this.customerRepository = Objects.requireNonNull(customerRepository, "customerRepository");
        this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository");
    }

    @Override
    public OrderPreviewDto previewOrder(String customerEmail)
    {
        Customer customer = findCustomer(customerEmail);
        Cart cart = requirePlaceableCart(customer);
        List<OrderLineDto> lines = new ArrayList<>();
        for (OrderLine line : snapshotLines(cart))
        {
            lines.add(toDto(line));
        }
        Address address = customer.getAddress();
        return new OrderPreviewDto(cart.getRestaurant().getName(), lines, cart.getSubtotal(),
                cart.getRestaurant().getDeliveryFee(), cart.getTotal(), address.district(), address.street(),
                PaymentMethod.CASH_ON_DELIVERY.name());
    }

    @Override
    public OrderDto placeOrder(String customerEmail)
    {
        Customer customer = findCustomer(customerEmail);
        Cart cart = requirePlaceableCart(customer);
        // Lines are copied before a number is issued so that a BR-1 violation does not consume a number.
        ArrayList<OrderLine> lines = snapshotLines(cart);

        Order order;
        try
        {
            order = new Order(orderRepository.nextOrderNumber(), customer, cart.getRestaurant(),
                    customer.getAddress(), lines);
        }
        catch (RuntimeException exception)
        {
            throw exception;
        }
        catch (Exception exception)
        {
            // Order currently reports its rules with a checked exception.
            throw new IllegalStateException(exception.getMessage(), exception);
        }
        order.setStatus(OrderStatus.PLACED);
        orderRepository.saveOrder(order);

        cart.emptyCart();
        return toDto(order);
    }

    private Customer findCustomer(String customerEmail)
    {
        if (customerEmail == null || customerEmail.isBlank())
        {
            throw new IllegalArgumentException("A customer must be identified before placing an order.");
        }
        return customerRepository.findCustomerByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer with e-mail '" + customerEmail.trim().toLowerCase(Locale.ROOT) + "' not found."));
    }

    /** BR-4: an order needs a delivery address and at least one cart line. */
    private static Cart requirePlaceableCart(Customer customer)
    {
        if (customer.getAddress() == null)
        {
            throw new IllegalStateException("A delivery address is required. Please complete the registration.");
        }
        Cart cart = customer.getCart();
        if (cart.getLines().isEmpty() || cart.getRestaurant() == null)
        {
            throw new IllegalStateException("The cart must contain at least one menu item.");
        }
        return cart;
    }

    /** BR-7: order lines keep the item name and price as they are now, not a reference to the menu item. */
    private static ArrayList<OrderLine> snapshotLines(Cart cart)
    {
        ArrayList<OrderLine> lines = new ArrayList<>();
        for (CartLine cartLine : cart.getLines())
        {
            try
            {
                lines.add(new OrderLine(cartLine.getMenuItem().getName(), cartLine.getMenuItem().getPrice(),
                        cartLine.getQuantity()));
            }
            catch (Exception exception)
            {
                // OrderLine currently reports BR-1 violations with a checked exception.
                throw new IllegalStateException("Cannot place the order: " + exception.getMessage(), exception);
            }
        }
        return lines;
    }

    private static OrderDto toDto(Order order)
    {
        List<OrderLineDto> lines = new ArrayList<>();
        for (OrderLine line : order.getLines())
        {
            lines.add(toDto(line));
        }
        Address address = order.getDeliveryAddress();
        return new OrderDto(order.getOrderNumber(), order.getStatus().name(), order.getPaymentMethod().name(),
                order.getRestaurant().getName(), lines, order.getSubtotal(), order.getDeliveryFee(),
                order.getTotal(), address.district(), address.street());
    }

    private static OrderLineDto toDto(OrderLine line)
    {
        return new OrderLineDto(line.getItemName(), line.getUnitPrice(), line.getQuantity(), line.getLineTotal());
    }
}
