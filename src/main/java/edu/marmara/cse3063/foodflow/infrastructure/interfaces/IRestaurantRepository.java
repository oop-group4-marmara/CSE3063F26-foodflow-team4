package edu.marmara.cse3063.foodflow.infrastructure.interfaces;

import edu.marmara.cse3063.foodflow.domain.models.Restaurant;

import java.util.ArrayList;

public interface IRestaurantRepository
{
    ArrayList<Restaurant> getAllRestaurants();
    Restaurant getRestaurantById(String id);
}
