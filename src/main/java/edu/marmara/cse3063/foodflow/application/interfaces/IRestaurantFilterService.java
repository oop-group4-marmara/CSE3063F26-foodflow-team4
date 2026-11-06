package edu.marmara.cse3063.foodflow.application.interfaces;

import edu.marmara.cse3063.foodflow.domain.models.RestaurantFilter;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.RestaurantRepository;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import java.util.ArrayList;
import java.util.List;
public interface IRestaurantFilterService
{
    public List<Restaurant> filter(RestaurantFilter filter);
}
