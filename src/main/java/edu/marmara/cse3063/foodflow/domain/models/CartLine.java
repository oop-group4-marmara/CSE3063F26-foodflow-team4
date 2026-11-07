package edu.marmara.cse3063.foodflow.domain.models;

import java.util.Objects;

public class CartLine
{
    private static final int MAX_QUANTITY = 20;

    private final MenuItem menuItem;
    private int quantity;

    public CartLine(MenuItem menuItem, int quantity)
    {
        setQuantity(quantity);
        this.menuItem = Objects.requireNonNull(menuItem, "menuItem");
    }

    public MenuItem getMenuItem()
    {
        return menuItem;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public final void setQuantity(int quantity)
    {
        if (quantity < 1 || quantity > MAX_QUANTITY)
        {
            throw new IllegalArgumentException("Quantity must be between 1 and " + MAX_QUANTITY + ".");
        }
        this.quantity = quantity;
    }

    public double getLineTotal()
    {
        return menuItem.getPrice() * quantity;
    }

}
