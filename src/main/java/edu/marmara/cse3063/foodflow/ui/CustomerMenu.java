package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.CustomerDto;
import edu.marmara.cse3063.foodflow.application.interfaces.ICustomerService;

import java.io.PrintStream;
import java.util.OptionalInt;

final class CustomerMenu
{
    private final ICustomerService customerService;
    private final RestaurantMenu restaurantMenu;
    private final CartMenu cartMenu;
    private final OrderMenu orderMenu;
    private final ConsoleInput input;
    private final PrintStream output;

    CustomerMenu(ICustomerService customerService, RestaurantMenu restaurantMenu, CartMenu cartMenu,
                 OrderMenu orderMenu, ConsoleInput input, PrintStream output)
    {
        this.customerService = customerService;
        this.restaurantMenu = restaurantMenu;
        this.cartMenu = cartMenu;
        this.orderMenu = orderMenu;
        this.input = input;
        this.output = output;
    }

    boolean show(CustomerDto customer)
    {
        output.println("================================");
        output.println("            FOODFLOW");
        output.println("Customer: " + customer.getName());
        output.println("E-mail: " + customer.getEmail());
        output.println("================================");
        output.println("1. Browse restaurants");
        output.println("2. Change customer");
        output.println("3. Place order");
        output.println("4. View cart");
        output.println("5. My orders");
        output.println("0. Exit");
        output.println();

        OptionalInt choice = input.readChoice("Your choice: ", 0, 1, 2, 3, 4, 5);
        if (choice.isEmpty() || choice.getAsInt() == 0) return false;
        if (choice.getAsInt() == 1) return restaurantMenu.browse(customer);
        if (choice.getAsInt() == 3) return orderMenu.place(customer);
        if (choice.getAsInt() == 4) return cartMenu.manage(customer);
        if (choice.getAsInt() == 5)
        {
            orderMenu.showOrders(customer);
            return true;
        }
        customerService.logout();
        return true;
    }
}
