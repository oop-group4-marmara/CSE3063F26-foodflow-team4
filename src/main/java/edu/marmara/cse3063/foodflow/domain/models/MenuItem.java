package edu.marmara.cse3063.foodflow.domain.models;

import java.util.Objects;

public class MenuItem
{

    private final String id;
    private String name;
    private String category;
    private double price;

    public MenuItem(String id, String name, String category, double price)
    {
        this.id = Objects.requireNonNull(id, "id");
        if (id.isBlank())
        {
            throw new IllegalArgumentException("id must not be blank.");
        }
        setName(name);
        setCategory(category);
        setPrice(price);
    }

    public String getId()
    {
        return id;
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

    public final void setPrice(double price)
    {
        if (price <= 0.0)
        {
            throw new IllegalArgumentException("Price must be positive");
        }
        this.price = price;
    }
}
