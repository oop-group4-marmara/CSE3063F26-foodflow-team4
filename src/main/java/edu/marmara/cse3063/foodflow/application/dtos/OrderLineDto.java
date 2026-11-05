package edu.marmara.cse3063.foodflow.application.dtos;

import edu.marmara.cse3063.foodflow.domain.models.OrderLine;

public class OrderLineDto
{
    private final String itemName;
    private final double unitPrice;
    private final int quantity;
    private final double lineTotal;

    public OrderLineDto(String itemName, double unitPrice, int quantity, double lineTotal)
    {
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    public static OrderLineDto from(OrderLine line)
    {
        return new OrderLineDto(
                line.getItemName(),
                line.getUnitPrice(),
                line.getQuantity(),
                line.getLineTotal());
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