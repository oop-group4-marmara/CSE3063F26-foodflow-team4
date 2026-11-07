package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.*;
import edu.marmara.cse3063.foodflow.application.interfaces.IOrderService;

import java.io.PrintStream;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

final class OrderMenu
{
    private final IOrderService orderService;
    private final ConsoleInput input;
    private final ConsoleRenderer renderer;
    private final PrintStream output;

    OrderMenu(IOrderService orderService, ConsoleInput input, ConsoleRenderer renderer, PrintStream output)
    {
        this.orderService = orderService;
        this.input = input;
        this.renderer = renderer;
        this.output = output;
    }

    boolean place(CustomerDto customer)
    {
        output.println();
        output.println("Place order");
        try
        {
            OrderPreviewDto preview = orderService.previewOrder(customer.getEmail());
            output.println("Restaurant: " + preview.getRestaurantName());
            renderer.printOrderLines(preview.getLines());
            renderer.printAmount("Subtotal", preview.getSubtotal());
            renderer.printAmount("Delivery fee", preview.getDeliveryFee());
            renderer.printAmount("Total", preview.getTotal());
            output.println("Delivery address: " + preview.getStreet() + ", " + preview.getDistrict());
            output.println("Payment method: " + ConsoleRenderer.formatPaymentMethod(preview.getPaymentMethod()));
            output.println();
            while (true)
            {
                Optional<String> answer = input.readLine("Confirm the order? (y/n): ");
                if (answer.isEmpty()) return false;
                String normalized = answer.get().strip().toLowerCase(Locale.ROOT);
                if (normalized.equals("n") || normalized.equals("no"))
                {
                    output.println("The order was not placed. Your cart is unchanged.");
                    return true;
                }
                if (normalized.equals("y") || normalized.equals("yes")) break;
                output.println("Please answer y or n.");
            }
            renderer.printReceipt(orderService.placeOrder(customer.getEmail()));
        }
        catch (IllegalArgumentException | IllegalStateException exception)
        {
            output.println("Error: " + exception.getMessage());
        }
        return true;
    }

    void showOrders(CustomerDto customer)
    {
        output.println();
        output.println("My orders");
        try
        {
            List<OrderDto> orders = orderService.getCustomerOrders(customer.getEmail());
            if (orders.isEmpty()) output.println("You have not placed any orders yet.");
            for (OrderDto order : orders)
            {
                output.println();
                output.println("Order #" + order.getOrderNumber() + " - " + order.getRestaurantName()
                        + " - " + order.getStatus());
                renderer.printOrderLines(order.getLines());
                renderer.printAmount("Total", order.getTotal());
            }
        }
        catch (IllegalArgumentException | IllegalStateException exception)
        {
            output.println("Error: " + exception.getMessage());
        }
    }
}
