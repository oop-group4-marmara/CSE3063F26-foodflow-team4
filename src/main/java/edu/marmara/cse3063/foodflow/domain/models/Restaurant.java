package edu.marmara.cse3063.foodflow.domain.models;

import java.util.ArrayList;
import java.util.Objects;

public class Restaurant
{
    private final String id;
    private String name;
    private String cuisine;
    private String city;
    private String district;
    private double deliveryFee;
    private final ArrayList<MenuItem> menuItems;

    public Restaurant(String id, String name, String cuisine, String city, String district, double deliveryFee)
    {
        this(id, name, cuisine, city, district, deliveryFee, new ArrayList<>());
    }

    public Restaurant(String id, String name, String cuisine, String city, String district, double deliveryFee, ArrayList<MenuItem> menuItems) //for creating a restaurant with a ready menu
    {
        this.id = requireText(id, "id");
        setName(name);
        setCuisine(cuisine);
        setCity(city);
        setDistrict(district);
        setDeliveryFee(deliveryFee);
        this.menuItems = new ArrayList<>(Objects.requireNonNull(menuItems, "menuItems"));
        for (MenuItem menuItem : this.menuItems)
        {
            Objects.requireNonNull(menuItem, "menuItem");
        }
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
        this.name = requireText(name, "name");
    }

    public String getCuisine()
    {
        return cuisine;
    }

    public final void setCuisine(String cuisine)
    {
        this.cuisine = requireText(cuisine, "cuisine");
    }

    /** Returns null when the restaurant's city is not supplied. */
    public String getCity()
    {
        return city;
    }

    public final void setCity(String city)
    {
        this.city = city == null ? null : requireText(city, "city");
    }

    public String getDistrict()
    {
        return district;
    }

    public final void setDistrict(String district)
    {
        this.district = requireText(district, "district");
    }

    public double getDeliveryFee()
    {
        return deliveryFee;
    }

    public final void setDeliveryFee(double deliveryFee)
    {
        if (!Double.isFinite(deliveryFee) || deliveryFee < 0.0)
        {
            throw new IllegalArgumentException("Delivery fee must be finite and non-negative.");
        }
        this.deliveryFee = deliveryFee;
    }

    public ArrayList<MenuItem> getMenuItems()
    {
        return new ArrayList<>(menuItems);
    }

    public void addMenuItem(MenuItem menuItem)
    {
        menuItems.add(Objects.requireNonNull(menuItem, "menuItem"));
    }

    private static String requireText(String value, String fieldName)
    {
        Objects.requireNonNull(value, fieldName);
        if (value.isBlank())
        {
            throw new IllegalArgumentException(fieldName + " must not be blank.");
        }
        return value;
    }
}
