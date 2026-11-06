package edu.marmara.cse3063.foodflow.application.dtos;

import edu.marmara.cse3063.foodflow.domain.models.MenuItem;

import java.util.Objects;

/** An immutable menu item shown to the Customer in UC4 View Menu. */
public final class MenuItemDto implements IDto
{
    private final String id;
    private final String name;
    private final String category;
    private final double price;

    public MenuItemDto(String id, String name, String category, double price)
    {
        this.id = Objects.requireNonNull(id, "id");
        this.name = Objects.requireNonNull(name, "name");
        this.category = Objects.requireNonNull(category, "category");
        this.price = price;
    }

    public static MenuItemDto fromMenuItem(MenuItem menuItem)
    {
        Objects.requireNonNull(menuItem, "menuItem");
        return new MenuItemDto(menuItem.getId(), menuItem.getName(), menuItem.getCategory(), menuItem.getPrice());
    }

    public String getId()
    {
        return id;
    }

    public String getName()
    {
        return name;
    }

    public String getCategory()
    {
        return category;
    }

    public double getPrice()
    {
        return price;
    }
}
