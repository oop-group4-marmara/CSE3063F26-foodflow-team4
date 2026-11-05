package edu.marmara.cse3063.foodflow.infrastructure.repositories;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.Strictness;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IRestaurantRepository;

import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class RestaurantRepository implements IRestaurantRepository
{
    private final Map<String, Restaurant> restaurants;

    public RestaurantRepository() throws IOException
    {
        this(Path.of("data", "restaurants.json"));
    }

    public RestaurantRepository(Path dataFile) throws IOException
    {
        restaurants = loadRestaurants(Objects.requireNonNull(dataFile, "dataFile"));
    }

    @Override
    public ArrayList<Restaurant> getAllRestaurants()
    {
        ArrayList<Restaurant> result = new ArrayList<>();
        for (Restaurant restaurant : restaurants.values())
        {
            result.add(copyRestaurant(restaurant));
        }
        return result;
    }

    @Override
    public Restaurant getRestaurantById(String id)
    {
        Restaurant restaurant = restaurants.get(Objects.requireNonNull(id, "id"));
        return restaurant == null ? null : copyRestaurant(restaurant);
    }

    private static Map<String, Restaurant> loadRestaurants(Path dataFile) throws IOException
    {
        Gson gson = new GsonBuilder().setStrictness(Strictness.STRICT).create();
        try (Reader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8))
        {
            JsonObject catalog = requireObject(gson.fromJson(reader, JsonElement.class), "catalog");
            JsonArray entries = requireArray(catalog.get("restaurants"), "restaurants");
            Map<String, Restaurant> loadedRestaurants = new LinkedHashMap<>();
            for (JsonElement entry : entries)
            {
                Restaurant restaurant = toRestaurant(requireObject(entry, "restaurant"));
                if (loadedRestaurants.putIfAbsent(restaurant.getId(), restaurant) != null)
                {
                    throw new IllegalArgumentException("Duplicate restaurant ID: " + restaurant.getId());
                }
            }
            return loadedRestaurants;
        }
        catch (RuntimeException exception)
        {
            throw new IOException("Invalid restaurant data in " + dataFile + ": " + exception.getMessage(),
                    exception);
        }
    }

    private static Restaurant toRestaurant(JsonObject data)
    {
        String id = requireString(data, "id");
        try
        {
            ArrayList<MenuItem> menuItems = new ArrayList<>();
            for (JsonElement entry : requireArray(data.get("menu"), "menu"))
            {
                JsonObject item = requireObject(entry, "menu item");
                menuItems.add(createMenuItem(requireString(item, "name"), requireString(item, "category"),
                        requireNumber(item, "price")));
            }
            // The current catalog omits city; do not invent a location for it.
            String city = data.has("city") && !data.get("city").isJsonNull()
                    ? requireString(data, "city") : null;
            return new Restaurant(id, requireString(data, "name"), requireString(data, "cuisine"), city,
                    requireString(data, "district"), requireNumber(data, "deliveryFee"), menuItems);
        }
        catch (RuntimeException exception)
        {
            throw new IllegalArgumentException("Restaurant " + id + ": " + exception.getMessage(), exception);
        }
    }

    private static Restaurant copyRestaurant(Restaurant restaurant)
    {
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        for (MenuItem item : restaurant.getMenuItems())
        {
            menuItems.add(createMenuItem(item.getName(), item.getCategory(), item.getPrice()));
        }
        return new Restaurant(restaurant.getId(), restaurant.getName(), restaurant.getCuisine(),
                restaurant.getCity(), restaurant.getDistrict(), restaurant.getDeliveryFee(), menuItems);
    }

    private static MenuItem createMenuItem(String name, String category, double price)
    {
        try
        {
            return new MenuItem(name, category, price);
        }
        catch (Exception exception)
        {
            // MenuItem currently reports state violations through a checked Exception.
            throw new IllegalArgumentException("Invalid menu item '" + name + "': " + exception.getMessage(),
                    exception);
        }
    }

    private static JsonObject requireObject(JsonElement value, String fieldName)
    {
        if (value == null || !value.isJsonObject())
        {
            throw new IllegalArgumentException(fieldName + " must be a JSON object.");
        }
        return value.getAsJsonObject();
    }

    private static JsonArray requireArray(JsonElement value, String fieldName)
    {
        if (value == null || !value.isJsonArray())
        {
            throw new IllegalArgumentException(fieldName + " must be a JSON array.");
        }
        return value.getAsJsonArray();
    }

    private static String requireString(JsonObject data, String fieldName)
    {
        JsonElement value = data.get(fieldName);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isString())
        {
            throw new IllegalArgumentException(fieldName + " must be a JSON string.");
        }
        return value.getAsString();
    }

    private static double requireNumber(JsonObject data, String fieldName)
    {
        JsonElement value = data.get(fieldName);
        if (value == null || !value.isJsonPrimitive() || !value.getAsJsonPrimitive().isNumber()
                || !Double.isFinite(value.getAsDouble()))
        {
            throw new IllegalArgumentException(fieldName + " must be a finite JSON number.");
        }
        return value.getAsDouble();
    }
}
