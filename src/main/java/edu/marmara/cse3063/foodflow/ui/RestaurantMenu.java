package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.*;
import edu.marmara.cse3063.foodflow.application.interfaces.ICartService;
import edu.marmara.cse3063.foodflow.application.interfaces.IRestaurantService;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

final class RestaurantMenu
{
    private final IRestaurantService restaurantService;
    private final ICartService cartService;
    private final ConsoleInput input;
    private final ConsoleRenderer renderer;
    private final PrintStream output;

    RestaurantMenu(IRestaurantService restaurantService, ICartService cartService,
                   ConsoleInput input, ConsoleRenderer renderer, PrintStream output)
    {
        this.restaurantService = restaurantService;
        this.cartService = cartService;
        this.input = input;
        this.renderer = renderer;
        this.output = output;
    }

    boolean browse(CustomerDto customer)
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
                output.println(cuisine.isEmpty() && district.isEmpty()
                        ? "No restaurants are available." : "No restaurants match the selected filters.");
            else renderer.printRestaurantTable(restaurants);
            output.println();
            output.println("1. Filter restaurants");
            output.println("2. Clear filters");
            output.println("3. View a menu");
            output.println("0. Back");
            output.println();
            OptionalInt choice = input.readChoice("Your choice: ", 0, 1, 2, 3);
            if (choice.isEmpty()) return false;
            if (choice.getAsInt() == 0) return true;
            if (choice.getAsInt() == 3)
            {
                if (!viewMenu(customer)) return false;
                continue;
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
            if (nextCuisine.isEmpty()) return false;
            if (nextCuisine.get().strip().equals("0")) continue;
            Optional<String> nextDistrict = input.readLine("District: ");
            if (nextDistrict.isEmpty()) return false;
            if (nextDistrict.get().strip().equals("0")) continue;
            cuisine = nextCuisine.get().strip();
            district = nextDistrict.get().strip();
            restaurants = cuisine.isEmpty() && district.isEmpty()
                    ? restaurantService.getAllRestaurants()
                    : restaurantService.filterRestaurants(cuisine, district);
        }
    }

    private boolean viewMenu(CustomerDto customer)
    {
        output.println("Enter 0 to cancel.");
        Optional<String> restaurantId = input.readLine("Restaurant ID: ");
        if (restaurantId.isEmpty()) return false;
        if (restaurantId.get().strip().equals("0")) return true;
        MenuDto menu;
        try { menu = restaurantService.viewMenu(restaurantId.get()); }
        catch (IllegalArgumentException exception)
        {
            output.println("Error: " + exception.getMessage());
            return true;
        }
        renderer.printMenu(menu);
        while (true)
        {
            output.println();
            output.println("1. Add an item to the cart");
            output.println("0. Back");
            output.println();
            OptionalInt choice = input.readChoice("Your choice: ", 0, 1);
            if (choice.isEmpty()) return false;
            if (choice.getAsInt() == 0) return true;
            if (!addToCart(customer, menu)) return false;
        }
    }

    private boolean addToCart(CustomerDto customer, MenuDto menu)
    {
        output.println("Enter 0 to cancel.");
        Optional<String> itemId = input.readLine("Item ID: ");
        if (itemId.isEmpty()) return false;
        if (itemId.get().strip().equals("0")) return true;
        Optional<String> quantity = input.readLine("Quantity: ");
        if (quantity.isEmpty()) return false;
        if (quantity.get().strip().equals("0")) return true;
        try
        {
            CartDto cart = cartService.addToCart(customer.getEmail(), menu.getRestaurantId(), itemId.get(),
                    parseQuantity(quantity.get()));
            output.println("Added to the cart.");
            renderer.printCart(cart);
        }
        catch (IllegalArgumentException | IllegalStateException exception)
        {
            output.println("Error: " + exception.getMessage());
        }
        return true;
    }

    static int parseQuantity(String text)
    {
        try { return Integer.parseInt(text.strip()); }
        catch (NumberFormatException exception)
        {
            throw new IllegalArgumentException("Quantity must be a whole number between 1 and 20.");
        }
    }
}
