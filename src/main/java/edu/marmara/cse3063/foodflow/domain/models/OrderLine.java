package edu.marmara.cse3063.foodflow.domain.models;

import java.util.Objects;

public class OrderLine
{

    private final String itemName;
    private final double unitPrice;
    private final int quantity;
    private final double lineTotal;

    public OrderLine(String itemName, double unitPrice, int quantity)
    {
        this.itemName = Objects.requireNonNull(itemName, "itemName");
        if (unitPrice <= 0.0 || quantity < 1)
        {
            throw new IllegalArgumentException("Unit price and quantity must be positive.");
        }
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = unitPrice * quantity;
    }

    public String getItemName()
    {
        return itemName;
    }

    public double getUnitPrice()
    {
        return unitPrice;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public double getLineTotal()
    {
        return lineTotal;
    }
}
