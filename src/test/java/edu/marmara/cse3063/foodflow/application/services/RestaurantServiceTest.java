package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.dtos.MenuDto;
import edu.marmara.cse3063.foodflow.application.dtos.MenuItemDto;
import edu.marmara.cse3063.foodflow.application.dtos.RestaurantDto;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IRestaurantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RestaurantServiceTest
{
    private final RestaurantService service = new RestaurantService(new CatalogRepository(List.of(
            new Restaurant("R01", "Kebapçı Mehmet", "Turkish", null, "Kadıköy", 25.0),
            new Restaurant("R02", "Pizza Roma", "Italian", null, "Beşiktaş", 40.0),
            new Restaurant("R03", "Burger Station", "American", null, "Şişli", 35.0),
            new Restaurant("R04", "Yeşil Tabak", "Vegan", null, "Kadıköy", 30.0),
            new Restaurant("R05", "Dönerci Usta", "Turkish", null, "Üsküdar", 20.0),
            new Restaurant("R06", "Tatlı Durağı", "Dessert", null, "Ataşehir", 30.0))));

    @Test
    void cuisineFilterMatchesSubstringsIgnoringCaseAndEdgeWhitespace()
    {
        assertEquals(List.of("R01", "R05"), ids(service.filterRestaurants("  tUr  ", null)));
    }

    @Test
    void districtFilterMatchesSubstringsIgnoringCaseAndEdgeWhitespace()
    {
        assertEquals(List.of("R01", "R04"), ids(service.filterRestaurants(null, "  KAD  ")));
    }

    @Test
    void bothFiltersMustMatchTheSameRestaurant()
    {
        assertEquals(List.of("R01"), ids(service.filterRestaurants("tur", "kad")));
        assertTrue(service.filterRestaurants("tur", "beşik").isEmpty());
    }

    @ParameterizedTest
    @MethodSource("unrestrictedCriteria")
    void absentCriteriaReturnTheWholeCatalogInItsOriginalOrder(String cuisine, String district)
    {
        assertEquals(List.of("R01", "R02", "R03", "R04", "R05", "R06"),
                ids(service.filterRestaurants(cuisine, district)));
    }

    @Test
    void blankCriterionDoesNotRestrictTheOtherCriterion()
    {
        assertEquals(List.of("R01", "R05"), ids(service.filterRestaurants("Tur", " \t ")));
        assertEquals(List.of("R02"), ids(service.filterRestaurants("\u2003", "beşik")));
    }

    @Test
    void unmatchedFilterReturnsAnEmptyList()
    {
        assertTrue(service.filterRestaurants("Japanese", null).isEmpty());
        assertTrue(service.filterRestaurants(null, "AnUnknownDistrict").isEmpty());
    }

    @Test
    void matchingHandlesTurkishCaseWithoutRemovingAccents()
    {
        assertEquals(List.of("R01", "R04"), ids(service.filterRestaurants(null, "KADIKÖY")));
        assertEquals(List.of("R03"), ids(service.filterRestaurants(null, "şİŞ")));
        assertTrue(service.filterRestaurants(null, "Kadikoy").isEmpty());
    }

    @Test
    void specialCharactersAreLiteralRatherThanRegularExpressions()
    {
        RestaurantService literalService = new RestaurantService(new CatalogRepository(List.of(
                new Restaurant("R99", "Literal Cafe", "A+B", null, "C.D", 10.0))));

        assertEquals(List.of("R99"), ids(literalService.filterRestaurants("A+", ".")));
        assertTrue(literalService.filterRestaurants("A.*", null).isEmpty());
    }

    @Test
    void eachNewFilterQueriesTheFullCatalogInsteadOfNarrowingPreviousResults()
    {
        assertEquals(List.of("R01", "R05"), ids(service.filterRestaurants("tur", null)));
        assertEquals(List.of("R02"), ids(service.filterRestaurants("ital", null)));
        assertEquals(List.of("R01", "R02", "R03", "R04", "R05", "R06"),
                ids(service.getAllRestaurants()));
    }

    @Test
    void menuGroupsTheItemsByCategoryInCatalogOrder()
    {
        MenuDto menu = menuService().viewMenu("R01");

        assertEquals(List.of("Main", "Drink"), List.copyOf(menu.getItemsByCategory().keySet()));
        assertEquals(List.of("Lahmacun", "Adana Kebap"), names(menu, "Main"));
        assertEquals(List.of("Ayran"), names(menu, "Drink"));
    }

    @Test
    void menuCarriesTheRestaurantNameAndItsDeliveryFee()
    {
        MenuDto menu = menuService().viewMenu("R01");

        assertEquals("R01", menu.getRestaurantId());
        assertEquals("Kebapçı Mehmet", menu.getRestaurantName());
        assertEquals(25.0, menu.getDeliveryFee());
        assertEquals(140.0, menu.getItemsByCategory().get("Main").getFirst().getPrice());
    }

    @Test
    void unknownRestaurantIdIsRefusedWithAMessageTheUiCanShow()
    {
        IllegalArgumentException refusal =
                assertThrows(IllegalArgumentException.class, () -> menuService().viewMenu("R99"));

        assertEquals("No restaurant has the ID R99.", refusal.getMessage());
    }

    @Test
    void blankRestaurantIdIsRefused()
    {
        assertThrows(IllegalArgumentException.class, () -> menuService().viewMenu(null));
        assertThrows(IllegalArgumentException.class, () -> menuService().viewMenu("   "));
    }

    private static List<String> names(MenuDto menu, String category)
    {
        return menu.getItemsByCategory().get(category).stream().map(MenuItemDto::getName).toList();
    }

    private static RestaurantService menuService()
    {
        ArrayList<MenuItem> menuItems = new ArrayList<>();
        try
        {
            menuItems.add(new MenuItem("R01-01", "Lahmacun", "Main", 140.0));
            menuItems.add(new MenuItem("R01-05", "Ayran", "Drink", 40.0));
            menuItems.add(new MenuItem("R01-02", "Adana Kebap", "Main", 260.0));
        }
        catch (Exception exception)
        {
            throw new AssertionError(exception);
        }
        return new RestaurantService(new CatalogRepository(List.of(
                new Restaurant("R01", "Kebapçı Mehmet", "Turkish", null, "Kadıköy", 25.0, menuItems))));
    }

    private static Stream<Arguments> unrestrictedCriteria()
    {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of("", ""),
                Arguments.of(" \t ", "\u2003"),
                Arguments.of(null, " "),
                Arguments.of(" ", null));
    }

    private static List<String> ids(List<RestaurantDto> restaurants)
    {
        return restaurants.stream().map(RestaurantDto::getId).toList();
    }

    private static final class CatalogRepository implements IRestaurantRepository
    {
        private final List<Restaurant> restaurants;

        private CatalogRepository(List<Restaurant> restaurants)
        {
            this.restaurants = restaurants;
        }

        @Override
        public ArrayList<Restaurant> getAllRestaurants()
        {
            return new ArrayList<>(restaurants);
        }

        @Override
        public Restaurant getRestaurantById(String id)
        {
            return restaurants.stream().filter(restaurant -> restaurant.getId().equals(id)).findFirst().orElse(null);
        }
    }
}
