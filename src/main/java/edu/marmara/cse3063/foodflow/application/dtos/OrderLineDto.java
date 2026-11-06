package edu.marmara.cse3063.foodflow.application.dtos;

import java.util.Objects;

/** One order line as recorded at placement: name, unit price, quantity and line total. */
public final class OrderLineDto implements IDto
{
    private final String itemName;
    private final double unitPrice;
    private final int quantity;
    private final double lineTotal;

    public OrderLineDto(String itemName, double unitPrice, int quantity, double lineTotal)
    {
        this.itemName = Objects.requireNonNull(itemName, "itemName");
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
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
