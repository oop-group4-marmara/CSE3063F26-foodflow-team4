package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.dtos.RestaurantDto;
import edu.marmara.cse3063.foodflow.application.interfaces.IRestaurantService;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.domain.models.RestaurantFilter;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IRestaurantRepository;

import java.util.ArrayList;
import java.util.List;
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
    public ArrayList<RestaurantDto> filterRestaurants(String cuisine, String district)
    {
        String cuisineFilter = cuisine == null ? "" : cuisine.strip();
        String districtFilter = district == null ? "" : district.strip();
        ArrayList<RestaurantDto> restaurants = new ArrayList<>();
        for (Restaurant restaurant : restaurantRepository.getAllRestaurants())
        {
            if (containsIgnoreCase(restaurant.getCuisine(), cuisineFilter)
                    && containsIgnoreCase(restaurant.getDistrict(), districtFilter))
            {
                restaurants.add(RestaurantDto.fromRestaurant(restaurant));
            }
        }
        return restaurants;
    }

    private static boolean containsIgnoreCase(String value, String filter)
    {
        for (int offset = 0; offset <= value.length() - filter.length(); offset++)
        {
            if (value.regionMatches(true, offset, filter, 0, filter.length()))
            {
                return true;
            }
        }
        return false;
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

    public List<Restaurant> filter(RestaurantFilter filter)
    {
        if(filter.isEmptyFilter()) return restaurantRepository.getAllRestaurants();

        return restaurantRepository.getAllRestaurants().stream()
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
