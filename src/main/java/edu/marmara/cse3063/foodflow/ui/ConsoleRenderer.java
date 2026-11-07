package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.*;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

final class ConsoleRenderer
{
    private final PrintStream output;

    ConsoleRenderer(PrintStream output)
    {
        this.output = output;
    }

    void printHeader()
    {
        output.println("================================");
        output.println("            FOODFLOW");
    }

    void printAmount(String label, double amount)
    {
        output.println(String.format(Locale.ROOT, "%s: %.2f TL", label, amount));
    }

    void printOrderLines(List<OrderLineDto> lines)
    {
        for (OrderLineDto line : lines)
        {
            output.println(String.format(Locale.ROOT, "  %d x %s @ %.2f TL = %.2f TL", line.getQuantity(),
                    line.getItemName(), line.getUnitPrice(), line.getLineTotal()));
        }
    }

    void printReceipt(OrderDto order)
    {
        output.println();
        output.println("================================");
        output.println("            RECEIPT");
        output.println("================================");
        output.println("Order number: " + order.getOrderNumber());
        output.println("Status: " + order.getStatus());
        output.println("Restaurant: " + order.getRestaurantName());
        printOrderLines(order.getLines());
        printAmount("Subtotal", order.getSubtotal());
        printAmount("Delivery fee", order.getDeliveryFee());
        printAmount("Total", order.getTotal());
        output.println("Delivery address: " + order.getStreet() + ", " + order.getDistrict());
        output.println("Payment method: " + formatPaymentMethod(order.getPaymentMethod()));
        output.println("================================");
    }

    void printCart(CartDto cart)
    {
        if (cart.getLines().isEmpty())
        {
            output.println("Your cart is empty.");
            return;
        }
        output.println("Restaurant: " + cart.getRestaurantName());
        for (CartLineDto line : cart.getLines())
        {
            output.println(String.format(Locale.ROOT, "  %-8s %d x %s @ %.2f TL = %.2f TL", line.getItemId(),
                    line.getQuantity(), line.getItemName(), line.getUnitPrice(), line.getLineTotal()));
        }
        printAmount("Subtotal", cart.getSubtotal());
        printAmount("Delivery fee", cart.getDeliveryFee());
        printAmount("Total", cart.getTotal());
    }

    void printMenu(MenuDto menu)
    {
        output.println();
        output.println("Menu - " + menu.getRestaurantName());
        if (menu.getItemsByCategory().isEmpty())
        {
            output.println("This restaurant has no menu items.");
        }
        for (Map.Entry<String, List<MenuItemDto>> category : menu.getItemsByCategory().entrySet())
        {
            output.println();
            output.println(category.getKey());
            for (MenuItemDto item : category.getValue())
            {
                output.println(String.format(Locale.ROOT, "  %-8s %-24s %8.2f TL",
                        item.getId(), item.getName(), item.getPrice()));
            }
        }
        output.println();
        printAmount("Delivery fee", menu.getDeliveryFee());
    }

    void printRestaurantTable(List<RestaurantDto> restaurants)
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

    static String formatPaymentMethod(String paymentMethod)
    {
        return paymentMethod.equals("CASH_ON_DELIVERY") ? "Cash on delivery" : paymentMethod;
    }
}
