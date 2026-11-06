package edu.marmara.cse3063.foodflow.application.interfaces;

import edu.marmara.cse3063.foodflow.application.dtos.RestaurantDto;

import java.util.ArrayList;

/** Restaurant queries available to the UI through Application DTOs. */
public interface IRestaurantService
{
    ArrayList<RestaurantDto> getAllRestaurants();

    /** Filters the full catalog by optional, case-insensitive cuisine and district substrings. */
    ArrayList<RestaurantDto> filterRestaurants(String cuisine, String district);

    RestaurantDto getRestaurantById(String id) throws Exception;
}
