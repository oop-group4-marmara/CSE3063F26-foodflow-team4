package edu.marmara.cse3063.foodflow.application.dtos;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** A Restaurant's Menu grouped by Category, the answer to viewMenu(restaurantId) in the SSD. */
public final class MenuDto implements IDto
{
    private final String restaurantId;
    private final String restaurantName;
    private final double deliveryFee;
    private final LinkedHashMap<String, List<MenuItemDto>> itemsByCategory;

    public MenuDto(String restaurantId, String restaurantName, double deliveryFee,
                   Map<String, List<MenuItemDto>> itemsByCategory)
    {
        this.restaurantId = Objects.requireNonNull(restaurantId, "restaurantId");
        this.restaurantName = Objects.requireNonNull(restaurantName, "restaurantName");
        this.deliveryFee = deliveryFee;
        Objects.requireNonNull(itemsByCategory, "itemsByCategory");
        this.itemsByCategory = new LinkedHashMap<>();
        itemsByCategory.forEach((category, items) -> this.itemsByCategory.put(category, List.copyOf(items)));
    }

    public String getRestaurantId()
    {
        return restaurantId;
    }

    public String getRestaurantName()
    {
        return restaurantName;
    }

    public double getDeliveryFee()
    {
        return deliveryFee;
    }

    /** Category name to its items, in the order they appear in the catalog. */
    public Map<String, List<MenuItemDto>> getItemsByCategory()
    {
        return Collections.unmodifiableMap(itemsByCategory);
    }
}
