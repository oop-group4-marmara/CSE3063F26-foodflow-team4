package edu.marmara.cse3063.foodflow.domain.models;

import java.util.Objects;

public class RestaurantFilter
{
    private String district;
    private String cuisine;

    public RestaurantFilter(String district, String cuisine)
    {
        this.district = (district==null?"" : district.trim());
        this.cuisine =  (cuisine ==null?"": cuisine.trim());
    }

    public String getDistrict(){return this.district;}
    public String getCuisine() {return this.cuisine;}

    public boolean isEmptyFilter()
    {
        return this.district.isEmpty() && this.cuisine.isEmpty();
    }

}
