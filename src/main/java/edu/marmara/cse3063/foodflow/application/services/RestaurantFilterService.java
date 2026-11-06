package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.interfaces.IRestaurantFilterService;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.domain.models.RestaurantFilter;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.RestaurantRepository;

import java.util.List;
import java.util.Map;

public class RestaurantFilterService implements IRestaurantFilterService
{
    private final RestaurantRepository repository;
    public RestaurantFilterService(RestaurantRepository repo)
    {
        repository = repo;
    }

    public List<Restaurant> filter(RestaurantFilter filter)
    {
        if(filter.isEmptyFilter()) return repository.getAllRestaurants();

        return repository.getAllRestaurants().stream()
                .filter(restaurant -> {return matchesDistrict(restaurant.getDistrict(),filter.getDistrict());})
                .filter(restaurant->{return matchesCuisine(restaurant.getCuisine(),filter.getCuisine());})
                .toList();
    }

    private boolean matchesCuisine(String c1 , String c2)
    {
        if(c2==null || c2.isEmpty()) return true;
        return c1.trim().equalsIgnoreCase(c2.trim());
    }
    private boolean matchesDistrict(String d1 , String d2)
    {
        if(d2==null || d2.isEmpty())return true;
        return d1.trim().equalsIgnoreCase(d2.trim());
    }
}
