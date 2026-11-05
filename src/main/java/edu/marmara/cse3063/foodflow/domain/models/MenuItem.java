package edu.marmara.cse3063.foodflow.domain.models;

import java.util.Objects;

public class MenuItem
{

    private String name;
    private String category;
    private double price;

    public MenuItem(String name, String category, double price) throws Exception
    {
        setName(name);
        setCategory(category);
        setPrice(price);
    }

    public String getName()
    {
        return name;
    }

    public final void setName(String name)
    {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getCategory()
    {
        return category;
    }

    public final void setCategory(String category)
    {
        this.category = Objects.requireNonNull(category, "category");
    }

    public double getPrice()
    {
        return price;
    }

    public final void setPrice(double price) throws Exception
    {
        if (price <= 0.0)
        {
            throw new Exception("Price must be positive");
        }
        this.price = price;
    }
}
