package edu.marmara.cse3063.foodflow.domain.models;

import java.util.ArrayList;
import java.util.Objects;

public class Restaurant
{
    private String name;
    private String cuisine;
    private String city;
    private String district;
    private double deliveryFee;
    private final ArrayList<MenuItem> menuItems;

    public Restaurant(String name, String cuisine, String city, String district, double deliveryFee)
    {
        this(name, cuisine, city, district, deliveryFee, new ArrayList<>());
    }

    public Restaurant(String name, String cuisine, String city, String district, double deliveryFee, ArrayList<MenuItem> menuItems) //for creating a restaurant with a ready menu
    {
        setName(name);
        setCuisine(cuisine);
        setCity(city);
        setDistrict(district);
        setDeliveryFee(deliveryFee);
        this.menuItems = new ArrayList<>(menuItems);
        for (MenuItem menuItem : this.menuItems)
        {
            Objects.requireNonNull(menuItem, "menuItem");
        }
    }

    public String getName()
    {
        return name;
    }

    public final void setName(String name)
    {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getCuisine()
    {
        return cuisine;
    }

    public final void setCuisine(String cuisine)
    {
        this.cuisine = Objects.requireNonNull(cuisine, "cuisine");
    }

    public String getCity()
    {
        return city;
    }

    public final void setCity(String city)
    {
        this.city = Objects.requireNonNull(city, "city");
    }

    public String getDistrict()
    {
        return district;
    }

    public final void setDistrict(String district)
    {
        this.district = Objects.requireNonNull(district, "district");
    }

    public double getDeliveryFee()
    {
        return deliveryFee;
    }

    public final void setDeliveryFee(double deliveryFee)
    {
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
}
