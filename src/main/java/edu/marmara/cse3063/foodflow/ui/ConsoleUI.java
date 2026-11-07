package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.CustomerDto;
import edu.marmara.cse3063.foodflow.application.interfaces.ICartService;
import edu.marmara.cse3063.foodflow.application.interfaces.ICustomerService;
import edu.marmara.cse3063.foodflow.application.interfaces.IOrderService;
import edu.marmara.cse3063.foodflow.application.interfaces.IRestaurantService;

import java.io.PrintStream;
import java.util.Objects;
import java.util.Optional;

public final class ConsoleUI
{
    private final ICustomerService customerService;
    private final WelcomeMenu welcomeMenu;
    private final CustomerMenu customerMenu;
    private final PrintStream output;

    public ConsoleUI(ICustomerService customerService, IRestaurantService restaurantService,
                     ICartService cartService, IOrderService orderService, ConsoleInput input, PrintStream output)
    {
        this.customerService = Objects.requireNonNull(customerService, "customerService");
        Objects.requireNonNull(restaurantService, "restaurantService");
        Objects.requireNonNull(cartService, "cartService");
        Objects.requireNonNull(orderService, "orderService");
        Objects.requireNonNull(input, "input");
        this.output = Objects.requireNonNull(output, "output");

        ConsoleRenderer renderer = new ConsoleRenderer(output);
        RestaurantMenu restaurantMenu = new RestaurantMenu(restaurantService, cartService, input, renderer, output);
        CartMenu cartMenu = new CartMenu(cartService, input, renderer, output);
        OrderMenu orderMenu = new OrderMenu(orderService, input, renderer, output);
        this.welcomeMenu = new WelcomeMenu(customerService, input, output);
        this.customerMenu = new CustomerMenu(customerService, restaurantMenu, cartMenu, orderMenu, input, output);
    }

    public void run()
    {
        boolean running = true;
        while (running)
        {
            Optional<CustomerDto> customer = customerService.getActiveCustomer();
            running = customer.isPresent()
                    ? customerMenu.show(customer.get())
                    : welcomeMenu.show();
            output.println();
        }
        output.println("Goodbye.");
        output.flush();
    }
}
