package edu.marmara.cse3063.foodflow.domain.models;

import java.util.ArrayList;

public class Cart
{
    private Restaurant restaurant;
    private ArrayList<CartLine> lines = new ArrayList<>();

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
        //ArrayList<CartLine>() is not working (idk why)
        this.lines = new ArrayList<>();
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
}