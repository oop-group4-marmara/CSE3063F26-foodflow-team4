package edu.marmara.cse3063.foodflow;

import edu.marmara.cse3063.foodflow.application.services.CustomerService;
import edu.marmara.cse3063.foodflow.application.services.OrderService;
import edu.marmara.cse3063.foodflow.application.services.RestaurantService;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryCustomerRepository;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryOrderRepository;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.RestaurantRepository;
import edu.marmara.cse3063.foodflow.ui.ConsoleInput;
import edu.marmara.cse3063.foodflow.ui.ConsoleUI;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * FoodFlow entry point.
 *
 * Keep this class tiny: it should only create the objects of your system,
 * connect them, and start the console user interface. No business rules here.
 */

public class App {

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            var customerRepository = new InMemoryCustomerRepository();
            var customerService = new CustomerService(customerRepository);
            var restaurantRepository = new RestaurantRepository();
            var restaurantService = new RestaurantService(restaurantRepository);
            var orderService = new OrderService(customerRepository, new InMemoryOrderRepository());
            var input = new ConsoleInput(scanner, System.out);
            new ConsoleUI(customerService, restaurantService, orderService, input, System.out).run();
        } catch (IOException exception) {
            System.err.println("Error: Could not load restaurant data: " + exception.getMessage());
        }
    }
}
