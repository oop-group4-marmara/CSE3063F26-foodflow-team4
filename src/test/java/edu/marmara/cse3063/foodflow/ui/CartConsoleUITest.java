package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.services.CartService;
import edu.marmara.cse3063.foodflow.application.services.CustomerService;
import edu.marmara.cse3063.foodflow.application.services.OrderService;
import edu.marmara.cse3063.foodflow.application.services.RestaurantService;
import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IRestaurantRepository;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryCustomerRepository;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(5)
class CartConsoleUITest
{
    private Customer customer;
    private CustomerService customerService;
    private RestaurantService restaurantService;
    private CartService cartService;
    private OrderService orderService;

    @BeforeEach
    void setUp() throws Exception
    {
        Restaurant kebap = new Restaurant("R01", "Kebapçı Mehmet", "Turkish", null, "Kadıköy", 25.0,
                new ArrayList<>(List.of(new MenuItem("R01-01", "Lahmacun", "Main", 140.0),
                        new MenuItem("R01-05", "Ayran", "Drink", 40.0))));
        Restaurant pizza = new Restaurant("R02", "Pizza Roma", "Italian", null, "Beşiktaş", 40.0,
                new ArrayList<>(List.of(new MenuItem("R02-01", "Margherita", "Pizza", 320.0))));
        IRestaurantRepository restaurants = new IRestaurantRepository()
        {
            @Override
            public ArrayList<Restaurant> getAllRestaurants()
            {
                return new ArrayList<>(List.of(kebap, pizza));
            }

            @Override
            public Restaurant getRestaurantById(String id)
            {
                return getAllRestaurants().stream().filter(r -> r.getId().equals(id)).findFirst().orElse(null);
            }
        };

        InMemoryCustomerRepository customers = new InMemoryCustomerRepository();
        customer = customers.saveCustomer(
                new Customer("Alice", "alice@example.com", new Address("Kadıköy", "Moda Street")));
        customerService = new CustomerService(customers);
        customerService.loginExisting("alice@example.com");
        restaurantService = new RestaurantService(restaurants);
        cartService = new CartService(restaurants, customers);
        orderService = new OrderService(customers, new InMemoryOrderRepository());
    }

    @Test
    void itemAddedFromTheMenuIsShownInTheCartWithTotals()
    {
        String output = run("1\n3\nR01\n1\nR01-01\n2\n0\n0\n4\n0\n0\n");

        assertTrue(output.contains("1. Add an item to the cart"));
        assertTrue(output.contains("Added to the cart."));
        assertTrue(output.contains("4. View cart"));
        assertTrue(output.contains("R01-01   2 x Lahmacun @ 140.00 TL = 280.00 TL"));
        assertTrue(output.contains("Subtotal: 280.00 TL"));
        assertTrue(output.contains("Total: 305.00 TL"));
        assertEquals(2, customer.getCart().getLines().getFirst().getQuantity());
    }

    @Test
    void refusedAdditionsAreReportedAndLeaveTheCartUnchanged()
    {
        String output = run("1\n3\nR01\n1\nR01-01\n21\n1\nR01-01\ntwo\n1\nR01-01\n1\n0\n"
                + "3\nR02\n1\nR02-01\n1\n0\n0\n0\n");

        assertTrue(output.contains("Error: Quantity must be between 1 and 20."));
        assertTrue(output.contains("Error: Quantity must be a whole number between 1 and 20."));
        assertTrue(output.contains("Error: Adding an item from another restaurant is refused."));
        assertEquals(1, customer.getCart().getLines().size());
        assertEquals("R01", customer.getCart().getRestaurant().getId());
    }

    @Test
    void cartQuantityCanBeChangedAndItemsRemoved()
    {
        String output = run("1\n3\nR01\n1\nR01-01\n1\n1\nR01-05\n1\n0\n0\n"
                + "4\n1\nR01-01\n3\n1\nR01-01\n25\n2\nR01-05\n0\n0\n");

        assertTrue(output.contains("The quantity was changed."));
        assertTrue(output.contains("Error: Quantity can't be higher than 20!"));
        assertTrue(output.contains("The item was removed from the cart."));
        assertEquals(1, customer.getCart().getLines().size());
        assertEquals(3, customer.getCart().getLines().getFirst().getQuantity());
    }

    @Test
    void emptyingTheCartLeavesNothingToOrder()
    {
        String output = run("1\n3\nR01\n1\nR01-01\n1\n0\n0\n4\n3\n3\n0\n");

        assertTrue(output.contains("The cart is now empty."));
        assertTrue(output.contains("Your cart is empty."));
        assertTrue(output.contains("Error: The cart must contain at least one menu item."));
        assertTrue(customer.getCart().getLines().isEmpty());
    }

    @Test
    void myOrdersListsThePlacedOrders()
    {
        String output = run("5\n1\n3\nR01\n1\nR01-01\n2\n0\n0\n3\ny\n5\n0\n");

        assertTrue(output.contains("You have not placed any orders yet."));
        assertTrue(output.contains("Order #1 - Kebapçı Mehmet - PLACED"));
        assertTrue(output.contains("Total: 305.00 TL"));
    }

    private String run(String script)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (Scanner scanner = new Scanner(script);
             PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8))
        {
            new ConsoleUI(customerService, restaurantService, cartService, orderService,
                    new ConsoleInput(scanner, output), output).run();
        }
        return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }
}
