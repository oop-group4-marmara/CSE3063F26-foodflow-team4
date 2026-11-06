package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.MenuDto;
import edu.marmara.cse3063.foodflow.application.dtos.RestaurantDto;
import edu.marmara.cse3063.foodflow.application.interfaces.IRestaurantService;
import edu.marmara.cse3063.foodflow.application.services.CartService;
import edu.marmara.cse3063.foodflow.application.services.CustomerService;
import edu.marmara.cse3063.foodflow.application.services.OrderService;
import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Cart;
import edu.marmara.cse3063.foodflow.domain.models.CartLine;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
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
class OrderConsoleUITest
{
    private InMemoryCustomerRepository customers;
    private CustomerService customerService;
    private OrderService orderService;
    private Customer customer;

    @BeforeEach
    void setUp() throws Exception
    {
        customers = new InMemoryCustomerRepository();
        customerService = new CustomerService(customers);
        orderService = new OrderService(customers, new InMemoryOrderRepository());
        customer = customers.saveCustomer(
                new Customer("Alice", "alice@example.com", new Address("Kadıköy", "Moda Street")));
        customerService.loginExisting("alice@example.com");
    }

    private void fillCart() throws Exception
    {
        MenuItem kebab = new MenuItem("M1", "Adana Kebap", "Main", 180.0);
        Cart cart = customer.getCart();
        cart.setRestaurant(new Restaurant("R01", "Kebapçı Mehmet", "Turkish", null, "Kadıköy", 25.0,
                new ArrayList<>(List.of(kebab))));
        cart.setLines(new ArrayList<>(List.of(new CartLine(kebab, 2))));
    }

    @Test
    void confirmedOrderPrintsReceiptAndEmptiesTheCart() throws Exception
    {
        fillCart();

        String output = run("3\ny\n0\n");

        assertTrue(output.contains("3. Place order"));
        assertTrue(output.contains("Confirm the order? (y/n): "));
        String receipt = output.substring(output.indexOf("RECEIPT"));
        assertTrue(receipt.contains("Order number: 1"));
        assertTrue(receipt.contains("Status: PLACED"));
        assertTrue(receipt.contains("Restaurant: Kebapçı Mehmet"));
        assertTrue(receipt.contains("2 x Adana Kebap @ 180.00 TL = 360.00 TL"));
        assertTrue(receipt.contains("Subtotal: 360.00 TL"));
        assertTrue(receipt.contains("Delivery fee: 25.00 TL"));
        assertTrue(receipt.contains("Total: 385.00 TL"));
        assertTrue(receipt.contains("Delivery address: Moda Street, Kadıköy"));
        assertTrue(receipt.contains("Payment method: Cash on delivery"));
        assertTrue(customer.getCart().getLines().isEmpty());
    }

    @Test
    void declinedConfirmationKeepsTheCart() throws Exception
    {
        fillCart();

        String output = run("3\nmaybe\nn\n0\n");

        assertTrue(output.contains("Please answer y or n."));
        assertTrue(output.contains("The order was not placed."));
        assertFalse(output.contains("RECEIPT"));
        assertEquals(1, customer.getCart().getLines().size());
    }

    @Test
    void emptyCartIsReportedWithoutAskingForConfirmation()
    {
        String output = run("3\n0\n");

        assertTrue(output.contains("Error: The cart must contain at least one menu item."));
        assertFalse(output.contains("Confirm the order?"));
        assertTrue(output.contains("Goodbye."));
    }

    private String run(String script)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (Scanner scanner = new Scanner(script);
             PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8))
        {
            new ConsoleUI(customerService, new NoRestaurantService(),
                    new CartService(new EmptyRestaurantRepository(), customers), orderService,
                    new ConsoleInput(scanner, output), output).run();
        }
        return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private static final class NoRestaurantService implements IRestaurantService
    {
        @Override
        public ArrayList<RestaurantDto> getAllRestaurants()
        {
            return new ArrayList<>();
        }

        @Override
        public ArrayList<RestaurantDto> filterRestaurants(String cuisine, String district)
        {
            return new ArrayList<>();
        }

        @Override
        public RestaurantDto getRestaurantById(String id)
        {
            return null;
        }

        @Override
        public MenuDto viewMenu(String restaurantId)
        {
            throw new AssertionError("Menus are not part of this fixture.");
        }
    }
}
