package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.dtos.OrderDto;
import edu.marmara.cse3063.foodflow.application.dtos.OrderLineDto;
import edu.marmara.cse3063.foodflow.application.dtos.OrderPreviewDto;
import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Cart;
import edu.marmara.cse3063.foodflow.domain.models.CartLine;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryCustomerRepository;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest
{
    private static final String EMAIL = "alice@example.com";

    private InMemoryCustomerRepository customers;
    private OrderService service;
    private Customer customer;
    private Restaurant restaurant;
    private MenuItem kebab;
    private MenuItem ayran;

    @BeforeEach
    void setUp() throws Exception
    {
        customers = new InMemoryCustomerRepository();
        service = new OrderService(customers, new InMemoryOrderRepository());
        customer = customers.saveCustomer(new Customer("Alice", EMAIL, new Address("Kadıköy", "Moda Street")));
        kebab = new MenuItem("M1", "Adana Kebap", "Main", 180.0);
        ayran = new MenuItem("M2", "Ayran", "Drink", 25.5);
        restaurant = new Restaurant("R01", "Kebapçı Mehmet", "Turkish", null, "Kadıköy", 30.0,
                new ArrayList<>(List.of(kebab, ayran)));
    }

    private void fillCart() throws Exception
    {
        Cart cart = customer.getCart();
        cart.setRestaurant(restaurant);
        cart.setLines(new ArrayList<>(List.of(new CartLine(kebab, 2), new CartLine(ayran, 3))));
    }

    @Test
    @DisplayName("BR-4: an empty cart is refused and no order number is issued")
    void emptyCartIsRefused() throws Exception
    {
        IllegalStateException error = assertThrows(IllegalStateException.class, () -> service.placeOrder(EMAIL));
        assertTrue(error.getMessage().contains("at least one menu item"));
        assertThrows(IllegalStateException.class, () -> service.previewOrder(EMAIL));

        fillCart();
        assertEquals(1, service.placeOrder(EMAIL).getOrderNumber());
    }

    @Test
    @DisplayName("BR-4: a customer without a delivery address is refused")
    void missingAddressIsRefused() throws Exception
    {
        Customer noAddress = new Customer("Bob", "bob@example.com", new Address("Beşiktaş", "Barbaros"))
        {
            @Override
            public Address getAddress()
            {
                return null;
            }
        };
        customers.saveCustomer(noAddress);
        noAddress.getCart().setRestaurant(restaurant);
        noAddress.getCart().setLines(new ArrayList<>(List.of(new CartLine(kebab, 1))));

        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.placeOrder("bob@example.com"));
        assertTrue(error.getMessage().contains("delivery address"));
        assertEquals(1, noAddress.getCart().getLines().size());
    }

    @Test
    void unknownCustomerIsRefused()
    {
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder("nobody@example.com"));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder(" "));
        assertThrows(IllegalArgumentException.class, () -> service.placeOrder(null));
    }

    @Test
    @DisplayName("Preview shows the lines and amounts without placing the order or emptying the cart")
    void previewDoesNotPlaceTheOrder() throws Exception
    {
        fillCart();

        OrderPreviewDto preview = service.previewOrder(EMAIL);

        assertEquals("Kebapçı Mehmet", preview.getRestaurantName());
        assertEquals(2, preview.getLines().size());
        assertEquals(436.5, preview.getSubtotal(), 1e-9);
        assertEquals(30.0, preview.getDeliveryFee(), 1e-9);
        assertEquals(466.5, preview.getTotal(), 1e-9);
        assertEquals("CASH_ON_DELIVERY", preview.getPaymentMethod());
        assertEquals(2, customer.getCart().getLines().size());
        assertEquals(1, service.placeOrder(EMAIL).getOrderNumber());
    }

    @Test
    @DisplayName("Placed order is PLACED, cash on delivery to the customer's address, total = subtotal + fee (BR-5, BR-6)")
    void placedOrderHasStatusPaymentAddressAndTotals() throws Exception
    {
        fillCart();

        OrderDto order = service.placeOrder(EMAIL);

        assertEquals("PLACED", order.getStatus());
        assertEquals("CASH_ON_DELIVERY", order.getPaymentMethod());
        assertEquals("Kebapçı Mehmet", order.getRestaurantName());
        assertEquals("Kadıköy", order.getDistrict());
        assertEquals("Moda Street", order.getStreet());
        assertEquals(436.5, order.getSubtotal(), 1e-9);
        assertEquals(30.0, order.getDeliveryFee(), 1e-9);
        assertEquals(order.getSubtotal() + order.getDeliveryFee(), order.getTotal(), 1e-9);

        OrderLineDto first = order.getLines().get(0);
        assertEquals("Adana Kebap", first.getItemName());
        assertEquals(180.0, first.getUnitPrice(), 1e-9);
        assertEquals(2, first.getQuantity());
        assertEquals(360.0, first.getLineTotal(), 1e-9);
    }

    @Test
    @DisplayName("BR-6: order numbers are unique and increasing")
    void orderNumbersIncrease() throws Exception
    {
        fillCart();
        long first = service.placeOrder(EMAIL).getOrderNumber();
        fillCart();
        long second = service.placeOrder(EMAIL).getOrderNumber();

        assertTrue(second > first);
    }

    @Test
    @DisplayName("BR-7: the cart is emptied and detached from its restaurant")
    void cartIsEmptiedAfterTheOrder() throws Exception
    {
        fillCart();

        service.placeOrder(EMAIL);

        assertTrue(customer.getCart().getLines().isEmpty());
        assertNull(customer.getCart().getRestaurant());
        assertThrows(IllegalStateException.class, () -> service.placeOrder(EMAIL));
    }

    @Test
    @DisplayName("BR-7: a later menu change does not alter the placed order")
    void orderKeepsItsOwnCopyOfLineData() throws Exception
    {
        fillCart();
        OrderDto order = service.placeOrder(EMAIL);

        kebab.setName("Renamed Kebap");
        kebab.setPrice(999.0);
        restaurant.setDeliveryFee(99.0);

        assertEquals("Adana Kebap", order.getLines().get(0).getItemName());
        assertEquals(466.5, order.getTotal(), 1e-9);
    }

    @Test
    @DisplayName("My orders: lists only the customer's own orders, oldest first")
    void listsOnlyTheCustomersOwnOrders() throws Exception
    {
        assertTrue(service.getCustomerOrders(EMAIL).isEmpty());

        fillCart();
        long first = service.placeOrder(EMAIL).getOrderNumber();
        fillCart();
        long second = service.placeOrder(EMAIL).getOrderNumber();

        Customer bob = customers.saveCustomer(new Customer("Bob", "bob@example.com", new Address("Beşiktaş", "Barbaros")));
        bob.getCart().setRestaurant(restaurant);
        bob.getCart().setLines(new ArrayList<>(List.of(new CartLine(ayran, 1))));
        service.placeOrder("bob@example.com");

        List<OrderDto> orders = service.getCustomerOrders(EMAIL.toUpperCase(Locale.ROOT));
        assertEquals(List.of(first, second), orders.stream().map(OrderDto::getOrderNumber).toList());
        assertEquals("Kebapçı Mehmet", orders.get(0).getRestaurantName());
    }

    @Test
    @DisplayName("My orders: an unknown customer is refused")
    void unknownCustomerCannotListOrders()
    {
        assertThrows(IllegalArgumentException.class, () -> service.getCustomerOrders("nobody@example.com"));
        assertThrows(IllegalArgumentException.class, () -> service.getCustomerOrders(" "));
    }
}
