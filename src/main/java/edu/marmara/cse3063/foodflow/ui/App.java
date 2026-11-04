package edu.marmara.cse3063.foodflow.ui;

/**
 * FoodFlow entry point.
 *
 * Keep this class tiny: it should only create the objects of your system,
 * connect them, and start the console user interface. No business rules here.
 */

import edu.marmara.cse3063.foodflow.domain.Customer;
import edu.marmara.cse3063.foodflow.infrastructure.InMemoryCustomerRepository;
import edu.marmara.cse3063.foodflow.application.CustomerService;

public class App {

    public static void main(String[] args) {
        System.out.println("FoodFlow - CSE3063 term project");
        System.out.println("Replace this with your console UI (Iteration 1).");
        // Suggested start (Iteration 1):
        //   RestaurantCatalog catalog = new Json....Loader("data/restaurants.json").load();
        //   ....
        //   new ConsoleUI(....).run();

        CustomerService service = new CustomerService(new InMemoryCustomerRepository());
        try {
            service.registerCustomer("   ", "ali@gmail.com", "Karliktepe Ave.", "Cam St.");
            System.out.println(service.getActiveCustomer().get().getName());
        }
        catch (IllegalArgumentException e )
        {
            System.out.println(e.getMessage());
        }
        try {
            service.registerCustomer("ali", "aligmail.com", "Karliktepe Ave.", "Cam St.");
            System.out.println(service.getActiveCustomer().get().getName());
        }
        catch (IllegalArgumentException e )
        {
            System.out.println(e.getMessage());
        }
        try {
            service.registerCustomer("ali", "", "Karliktepe Ave.", "Cam St.");
            System.out.println(service.getActiveCustomer().get().getName());
        }
        catch (IllegalArgumentException e )
        {
            System.out.println(e.getMessage());
        }
        try {
            service.registerCustomer("ali", "ali@gmail.com", "", "Cam St.");
            System.out.println(service.getActiveCustomer().get().getName());
        }
        catch (IllegalArgumentException e )
        {
            System.out.println(e.getMessage());
        }
        try {
            service.registerCustomer("ali", "ali@gmail.com", "Karliktepe Ave.", "");
            System.out.println(service.getActiveCustomer().get().getName());
        }
        catch (IllegalArgumentException e )
        {
            System.out.println(e.getMessage());
        }

            service.registerCustomer("ali", "ali@gmail.com", "Karliktepe Ave.", "Cam St.");
            System.out.println(service.getActiveCustomer().get().getName());






    }
}
