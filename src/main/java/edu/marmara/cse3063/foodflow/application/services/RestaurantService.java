package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.dtos.RestaurantDto;
import edu.marmara.cse3063.foodflow.application.interfaces.IRestaurantService;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IRestaurantRepository;

import java.util.ArrayList;
import java.util.Objects;

public class RestaurantService implements IRestaurantService
{
    private final IRestaurantRepository restaurantRepository;

    public RestaurantService(IRestaurantRepository restaurantRepository)
    {
        this.restaurantRepository = Objects.requireNonNull(restaurantRepository, "restaurantRepository");
    }

    @Override
    public ArrayList<RestaurantDto> getAllRestaurants()
    {
        ArrayList<RestaurantDto> restaurants = new ArrayList<>();
        for (Restaurant restaurant : restaurantRepository.getAllRestaurants())
        {
            restaurants.add(RestaurantDto.fromRestaurant(restaurant));
        }
        return restaurants;
    }

    @Override
    public RestaurantDto getRestaurantById(String id) throws Exception {
        if (id == null || id.isBlank())
        {
            throw new Exception("Restaurant ID must not be null or blank.");
        }
        Restaurant restaurant = restaurantRepository.getRestaurantById(id);
        return restaurant == null ? null : RestaurantDto.fromRestaurant(restaurant);
    }
}
