package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.CustomerDto;
import edu.marmara.cse3063.foodflow.application.dtos.RegisterCustomerDto;
import edu.marmara.cse3063.foodflow.application.dtos.RestaurantDto;
import edu.marmara.cse3063.foodflow.application.interfaces.ICustomerService;
import edu.marmara.cse3063.foodflow.application.interfaces.IRestaurantService;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;

public final class ConsoleUI
{
    private final ICustomerService customerService;
    private final IRestaurantService restaurantService;
    private final ConsoleInput input;
    private final PrintStream output;

    public ConsoleUI(ICustomerService customerService, IRestaurantService restaurantService,
                     ConsoleInput input, PrintStream output)
    {
        this.customerService = Objects.requireNonNull(customerService, "customerService");
        this.restaurantService = Objects.requireNonNull(restaurantService, "restaurantService");
        this.input = Objects.requireNonNull(input, "input");
        this.output = Objects.requireNonNull(output, "output");
    }

    public void run()
    {
        boolean running = true;
        while (running)
        {
            Optional<CustomerDto> customer = customerService.getActiveCustomer();
            running = customer.isPresent()
                    ? showCustomerMenu(customer.get())
                    : showWelcomeMenu();
            output.println();
        }
        output.println("Goodbye.");
        output.flush();
    }

    private boolean showWelcomeMenu()
    {
        printHeader();
        output.println("================================");
        output.println("1. Register a new customer");
        output.println("2. Continue with an existing e-mail");
        output.println("0. Exit");
        output.println();

        OptionalInt choice = input.readChoice("Your choice: ", 0, 1, 2);
        if (choice.isEmpty() || choice.getAsInt() == 0)
        {
            return false;
        }
        return choice.getAsInt() == 1 ? registerCustomer() : loginCustomer();
    }

    private boolean showCustomerMenu(CustomerDto customer)
    {
        printHeader();
        output.println("Customer: " + customer.getName());
        output.println("E-mail: " + customer.getEmail());
        output.println("================================");
        output.println("1. Browse restaurants");
        output.println("2. Change customer");
        output.println("0. Exit");
        output.println();

        OptionalInt choice = input.readChoice("Your choice: ", 0, 1, 2);
        if (choice.isEmpty() || choice.getAsInt() == 0)
        {
            return false;
        }
        if (choice.getAsInt() == 1)
        {
            return browseRestaurants();
        }
        customerService.logout();
        return true;
    }

    private boolean browseRestaurants()
    {
        String cuisine = "";
        String district = "";
        ArrayList<RestaurantDto> restaurants = restaurantService.getAllRestaurants();

        while (true)
        {
            output.println();
            output.println("Restaurants");
            output.println("Cuisine: " + (cuisine.isEmpty() ? "All" : cuisine)
                    + " | District: " + (district.isEmpty() ? "All" : district));
            if (restaurants.isEmpty())
            {
                output.println(cuisine.isEmpty() && district.isEmpty()
                        ? "No restaurants are available."
                        : "No restaurants match the selected filters.");
            }
            else
            {
                printRestaurantTable(restaurants);
            }
            output.println();
            output.println("1. Filter restaurants");
            output.println("2. Clear filters");
            output.println("0. Back");
            output.println();

            OptionalInt choice = input.readChoice("Your choice: ", 0, 1, 2);
            if (choice.isEmpty())
            {
                return false;
            }
            if (choice.getAsInt() == 0)
            {
                return true;
            }
            if (choice.getAsInt() == 2)
            {
                restaurants = restaurantService.getAllRestaurants();
                cuisine = "";
                district = "";
                continue;
            }

            output.println("Leave a field blank for all values. Enter 0 to cancel.");
            Optional<String> nextCuisine = input.readLine("Cuisine: ");
            if (nextCuisine.isEmpty())
            {
                return false;
            }
            if (nextCuisine.get().strip().equals("0"))
            {
                continue;
            }
            Optional<String> nextDistrict = input.readLine("District: ");
            if (nextDistrict.isEmpty())
            {
                return false;
            }
            if (nextDistrict.get().strip().equals("0"))
            {
                continue;
            }

            String submittedCuisine = nextCuisine.get().strip();
            String submittedDistrict = nextDistrict.get().strip();
            ArrayList<RestaurantDto> results = submittedCuisine.isEmpty() && submittedDistrict.isEmpty()
                    ? restaurantService.getAllRestaurants()
                    : restaurantService.filterRestaurants(submittedCuisine, submittedDistrict);
            cuisine = submittedCuisine;
            district = submittedDistrict;
            restaurants = results;
        }
    }

    private void printRestaurantTable(List<RestaurantDto> restaurants)
    {
        List<String[]> rows = new ArrayList<>();
        rows.add(new String[]{"ID", "Name", "Cuisine", "District", "Delivery fee"});
        for (RestaurantDto restaurant : restaurants)
        {
            rows.add(new String[]{restaurant.getId(), restaurant.getName(), restaurant.getCuisine(),
                    restaurant.getDistrict(), String.format(Locale.ROOT, "%.2f TL", restaurant.getDeliveryFee())});
        }
        int[] widths = new int[5];
        for (String[] row : rows)
        {
            for (int column = 0; column < widths.length; column++)
            {
                widths[column] = Math.max(widths[column], row[column].length());
            }
        }
        String format = "%-" + widths[0] + "s | %-" + widths[1] + "s | %-" + widths[2]
                + "s | %-" + widths[3] + "s | %" + widths[4] + "s%n";
        for (String[] row : rows)
        {
            output.printf(Locale.ROOT, format, (Object[]) row);
        }
    }

    private boolean registerCustomer()
    {
        output.println();
        output.println("Register a new customer");
        output.println("Enter 0 to go back.");

        String[] prompts = {"Name: ", "E-mail: ", "District: ", "Street: "};
        String[] fields = new String[prompts.length];
        try
        {
            for (int index = 0; index < prompts.length; index++)
            {
                Optional<String> value = input.readLine(prompts[index]);
                if (value.isEmpty())
                {
                    return false;
                }
                if (value.get().trim().equals("0"))
                {
                    return true;
                }
                fields[index] = value.get();
                if (index == 1)
                {
                    customerService.validateRegistrationEmail(fields[index]);
                }
            }
            customerService.registerCustomer(new RegisterCustomerDto(fields[0], fields[1], fields[2], fields[3]));
        }
        catch (IllegalArgumentException | IllegalStateException exception)
        {
            output.println("Error: " + exception.getMessage());
        }
        return true;
    }

    private boolean loginCustomer()
    {
        output.println();
        output.println("Continue with an existing e-mail");
        output.println("Enter 0 to go back.");

        Optional<String> email = input.readLine("E-mail: ");
        if (email.isEmpty())
        {
            return false;
        }
        if (email.get().trim().equals("0"))
        {
            return true;
        }

        try
        {
            customerService.loginExisting(email.get());
        }
        catch (IllegalArgumentException | IllegalStateException exception)
        {
            output.println("Error: " + exception.getMessage());
        }
        return true;
    }

    private void printHeader()
    {
        output.println("================================");
        output.println("            FOODFLOW");
    }
}
