package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.application.repositories.IRestaurantRepository;

import java.util.ArrayList;

/** Backs a CartService in UI tests that never add items to the cart. */
final class EmptyRestaurantRepository implements IRestaurantRepository
{
    @Override
    public ArrayList<Restaurant> getAllRestaurants()
    {
        return new ArrayList<>();
    }

    @Override
    public Restaurant getRestaurantById(String id)
    {
        return null;
    }
}
