package edu.marmara.cse3063.foodflow.application.interfaces;

import edu.marmara.cse3063.foodflow.application.dtos.RestaurantDto;

import java.util.ArrayList;

/** Restaurant queries available to the UI through Application DTOs. */
public interface IRestaurantService
{
    ArrayList<RestaurantDto> getAllRestaurants();

    RestaurantDto getRestaurantById(String id) throws Exception;
}
