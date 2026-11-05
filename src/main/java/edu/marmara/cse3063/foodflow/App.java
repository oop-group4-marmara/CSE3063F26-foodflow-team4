package edu.marmara.cse3063.foodflow;

import edu.marmara.cse3063.foodflow.application.services.CustomerService;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryCustomerRepository;
import edu.marmara.cse3063.foodflow.ui.ConsoleInput;
import edu.marmara.cse3063.foodflow.ui.ConsoleUI;

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
            var repository = new InMemoryCustomerRepository();
            var customerService = new CustomerService(repository);
            var input = new ConsoleInput(scanner, System.out);
            new ConsoleUI(customerService, input, System.out).run();
        }
    }
}
