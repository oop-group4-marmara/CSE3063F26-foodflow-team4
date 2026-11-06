package edu.marmara.cse3063.foodflow.application.dtos;

public final class CartLineDto implements IDto {
    private final String itemId;
    private final String itemName;
    private final double unitPrice;
    private final int quantity;
    private final double lineTotal;

    public CartLineDto(String itemId, String itemName, double unitPrice, int quantity, double lineTotal) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
        this.lineTotal = lineTotal;
    }

    public String getItemId() { return itemId; }
    public String getItemName() { return itemName; }
    public double getUnitPrice() { return unitPrice; }
    public int getQuantity() { return quantity; }
    public double getLineTotal() { return lineTotal; }
}