package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.CartDto;
import edu.marmara.cse3063.foodflow.application.dtos.CustomerDto;
import edu.marmara.cse3063.foodflow.application.interfaces.ICartService;

import java.io.PrintStream;
import java.util.Optional;
import java.util.OptionalInt;

final class CartMenu
{
    private final ICartService cartService;
    private final ConsoleInput input;
    private final ConsoleRenderer renderer;
    private final PrintStream output;

    CartMenu(ICartService cartService, ConsoleInput input, ConsoleRenderer renderer, PrintStream output)
    {
        this.cartService = cartService;
        this.input = input;
        this.renderer = renderer;
        this.output = output;
    }

    boolean manage(CustomerDto customer)
    {
        while (true)
        {
            output.println();
            output.println("Cart");
            CartDto cart = cartService.getCart(customer.getEmail());
            renderer.printCart(cart);
            if (cart.getLines().isEmpty()) return true;
            output.println();
            output.println("1. Change a quantity");
            output.println("2. Remove an item");
            output.println("3. Empty the cart");
            output.println("0. Back");
            output.println();
            OptionalInt choice = input.readChoice("Your choice: ", 0, 1, 2, 3);
            if (choice.isEmpty()) return false;
            if (choice.getAsInt() == 0) return true;
            if (choice.getAsInt() == 3)
            {
                cartService.emptyCart(customer.getEmail());
                output.println("The cart is now empty.");
                continue;
            }
            output.println("Enter 0 to cancel.");
            Optional<String> itemId = input.readLine("Item ID: ");
            if (itemId.isEmpty()) return false;
            if (itemId.get().strip().equals("0")) continue;
            try
            {
                if (choice.getAsInt() == 2)
                {
                    cartService.removeFromCart(customer.getEmail(), itemId.get());
                    output.println("The item was removed from the cart.");
                    continue;
                }
                Optional<String> quantity = input.readLine("New quantity: ");
                if (quantity.isEmpty()) return false;
                if (quantity.get().strip().equals("0")) continue;
                cartService.changeCartQuantity(customer.getEmail(), itemId.get(),
                        RestaurantMenu.parseQuantity(quantity.get()));
                output.println("The quantity was changed.");
            }
            catch (IllegalArgumentException | IllegalStateException exception)
            {
                output.println("Error: " + exception.getMessage());
            }
        }
    }
}
