package edu.marmara.cse3063.foodflow.application.dtos;

import edu.marmara.cse3063.foodflow.domain.models.Restaurant;

import java.util.Objects;

/** An immutable restaurant summary for UC2; menu entities stay inside the system. */
public final class RestaurantDto implements IDto
{
    private final String id;
    private final String name;
    private final String cuisine;
    private final String district;
    private final double deliveryFee;

    public RestaurantDto(String id, String name, String cuisine, String district, double deliveryFee)
    {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.cuisine = Objects.requireNonNull(cuisine, "cuisine");
        this.district = Objects.requireNonNull(district, "district");
        this.deliveryFee = deliveryFee;
    }

    /** Called by Application code before returning results to the UI. */
    public static RestaurantDto fromRestaurant(Restaurant restaurant)
    {
        Objects.requireNonNull(restaurant, "restaurant");
        return new RestaurantDto(restaurant.getId(), restaurant.getName(), restaurant.getCuisine(),
                restaurant.getDistrict(), restaurant.getDeliveryFee());
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getCuisine()
    {
        return cuisine;
    }

    public String getDistrict()
    {
        return district;
    }

    public double getDeliveryFee()
    {
        return deliveryFee;
    }
}
