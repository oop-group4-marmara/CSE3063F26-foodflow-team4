package edu.marmara.cse3063.foodflow.domain.models;

import java.util.ArrayList;

public class Cart
{
    private Restaurant restaurant;
    private ArrayList<CartLine> lines = new ArrayList<>();

    private CartLine findLine(MenuItem item) {
        for (CartLine line : lines) {
            if (line.getMenuItem().getId().equals(item.getId())) {
                return line;
            }
        }
        return null;
    }

    public Restaurant getRestaurant()
    {
        return restaurant;
    }

    public void setRestaurant(Restaurant restaurant)
    {
        this.restaurant = restaurant;
    }

    public ArrayList<CartLine> getLines()
    {
        return new ArrayList<>(lines);
    }

    public void setLines(ArrayList<CartLine> lines)
    {
        this.lines = new ArrayList<>(lines);
    }

    public void deleteCartLine(CartLine cartLine) {
        lines.remove(cartLine);
        return;
    }

    public void emptyCart() {
        this.lines = new ArrayList<CartLine>();
        this.restaurant = null;
        return;
    }

    public double getSubtotal()
    {
        double subtotal = 0.0;
        for (CartLine line : lines)
        {
            subtotal += line.getLineTotal();
        }
        return subtotal;
    }

    public double getTotal()
    {
        return restaurant == null ? 0.0 : getSubtotal() + restaurant.getDeliveryFee();
    }


    public void addItem(MenuItem item, int quantity, Restaurant restaurant) throws Exception {
        if (quantity < 1 || quantity > 20) throw new IllegalArgumentException("Quantity must be between 1 and 20.");
        if (this.restaurant == null || this.lines.isEmpty()) this.restaurant = restaurant;


        // 2c. Customer adds an item from a different Restaurant's menu. System refuses the request and suggests emptying the cart to continue. (BR-2)
        if (!this.restaurant.getId().equals(restaurant.getId())) throw new Exception("Adding an item from another restaurant is refused. Please empty the cart to continue.");

        CartLine line = findLine(item);

        if (line != null) {
            int currentQuantity = line.getQuantity();
            int newQuantity = currentQuantity + quantity;

            if (newQuantity > 20) throw new Exception("Cannot add item. Total quantity would exceed the maximum limit of 20.");

            line.setQuantity(newQuantity);
        }

        else lines.add(new CartLine(item, quantity));

        return;
    }

    public void removeItem(MenuItem item) throws Exception {
        CartLine line = findLine(item);

        if (line == null) throw new Exception("Item is not found in the cart!");

        int currentQuantity = line.getQuantity();
        if (currentQuantity <= 1) this.deleteCartLine(line);

        else line.setQuantity(currentQuantity - 1);

        return;
    }

    public void changeQuantity(MenuItem item, Integer newQuantity) throws Exception {
        // 2a. The quantity is not in the allowed range of 1 to 20. System refuses the addition and reports that the quantity must be between 1 and 20. (BR-1)
        // 4a. The new quantity is outside the range 1 to 20. System refuses the change. (BR-1)
        if (newQuantity > 20) throw new IllegalArgumentException("Quantity can't be higher than 20!");
        if (newQuantity < 0) throw new IllegalArgumentException("Quantity can't be lower than 0!");

        CartLine line = findLine(item);

        if (newQuantity == 0) {
            if (line != null) this.deleteCartLine(line);
        }

        else {
            if (line != null) line.setQuantity(newQuantity);
            else lines.add(new CartLine(item, newQuantity));
        }

        return;
    }


}
