package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.CustomerDto;
import edu.marmara.cse3063.foodflow.application.dtos.MenuDto;
import edu.marmara.cse3063.foodflow.application.dtos.MenuItemDto;
import edu.marmara.cse3063.foodflow.application.dtos.RegisterCustomerDto;
import edu.marmara.cse3063.foodflow.application.dtos.RestaurantDto;
import edu.marmara.cse3063.foodflow.application.interfaces.ICustomerService;
import edu.marmara.cse3063.foodflow.application.interfaces.IRestaurantService;
import edu.marmara.cse3063.foodflow.application.services.OrderService;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryCustomerRepository;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryOrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Queue;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(5)
class RestaurantConsoleUITest
{
    private static final RestaurantDto TURKISH = new RestaurantDto(
            "R01", "Kebapçı Mehmet", "Turkish", "Kadıköy", 25.0);
    private static final RestaurantDto VEGAN = new RestaurantDto(
            "R04", "Yeşil Tabak", "Vegan", "Kadıköy", 30.0);
    private static final RestaurantDto ITALIAN = new RestaurantDto(
            "R02", "Pizza Roma", "Italian", "Beşiktaş", 40.0);
    private static final List<RestaurantDto> CATALOG = List.of(TURKISH, VEGAN, ITALIAN);
    private static final MenuDto TURKISH_MENU = new MenuDto("R01", "Kebapçı Mehmet", 25.0, turkishMenu());

    /** A LinkedHashMap so that the fixture has the catalog order the service guarantees. */
    private static Map<String, List<MenuItemDto>> turkishMenu()
    {
        Map<String, List<MenuItemDto>> itemsByCategory = new LinkedHashMap<>();
        itemsByCategory.put("Main", List.of(new MenuItemDto("R01-01", "Lahmacun", "Main", 140.0),
                new MenuItemDto("R01-02", "Adana Kebap", "Main", 260.0)));
        itemsByCategory.put("Drink", List.of(new MenuItemDto("R01-05", "Ayran", "Drink", 40.0)));
        return itemsByCategory;
    }

    @Test
    @ResourceLock("java.util.Locale")
    void listsAllColumnsInServiceOrderWithLocaleIndependentFeesAndKeepsTheCustomer()
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(CATALOG);
        ActiveCustomerService customers = new ActiveCustomerService();
        Locale original = Locale.getDefault();
        String output;
        try
        {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            output = run("1\n0\n0\n", customers, restaurants);
        }
        finally
        {
            Locale.setDefault(original);
        }

        String table = screens(output).getFirst();
        assertTrue(table.contains("ID"));
        assertTrue(table.contains("Name"));
        assertTrue(table.contains("Cuisine"));
        assertTrue(table.contains("District"));
        assertTrue(table.contains("Delivery fee"));
        assertTrue(table.contains("R01"));
        assertTrue(table.contains("Turkish"));
        assertTrue(table.contains("Kadıköy"));
        assertTrue(table.contains("25.00 TL"));
        assertFalse(table.contains("25,00 TL"));
        assertTrue(table.indexOf("Kebapçı Mehmet") < table.indexOf("Yeşil Tabak"));
        assertTrue(table.indexOf("Yeşil Tabak") < table.indexOf("Pizza Roma"));
        assertEquals(1, restaurants.allCalls);
        assertTrue(restaurants.queries.isEmpty());
        assertEquals(2, occurrences(output, "Customer: Alice"));
        assertEquals(0, customers.logoutCalls);
        assertEquals("alice@example.com", customers.getActiveCustomer().orElseThrow().getEmail());
    }

    @Test
    void emptyCatalogShowsItsOwnMessageAndStillAllowsBack()
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(List.of());
        String output = run("1\n0\n0\n", new ActiveCustomerService(), restaurants);

        assertTrue(output.contains("No restaurants are available."));
        assertFalse(output.contains("No restaurants match the selected filters."));
        assertTrue(output.contains("1. Filter restaurants"));
        assertTrue(output.contains("2. Clear filters"));
        assertTrue(output.contains("0. Back"));
        assertEquals(2, occurrences(output, "Customer: Alice"));
    }

    @Test
    void completedFiltersReplacePreviousCriteriaAndClearRestoresAllRows()
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(CATALOG);
        restaurants.filterResponses.add(List.of(TURKISH));
        restaurants.filterResponses.add(List.of(ITALIAN));

        String output = run("1\n1\n Tur \n Kad \n1\nIta\n\n2\n0\n0\n",
                new ActiveCustomerService(), restaurants);

        assertEquals(List.of(new FilterQuery("Tur", "Kad"), new FilterQuery("Ita", "")), restaurants.queries);
        assertEquals(2, restaurants.allCalls);
        List<String> screens = screens(output);
        assertEquals(4, screens.size());
        assertTrue(screens.get(1).contains("Cuisine: Tur | District: Kad"));
        assertTrue(screens.get(1).contains("Kebapçı Mehmet"));
        assertFalse(screens.get(1).contains("Pizza Roma"));
        assertTrue(screens.get(2).contains("Cuisine: Ita | District: All"));
        assertTrue(screens.get(2).contains("Pizza Roma"));
        assertFalse(screens.get(2).contains("Kebapçı Mehmet"));
        assertTrue(screens.get(3).contains("Cuisine: All | District: All"));
        assertTrue(screens.get(3).contains("Kebapçı Mehmet"));
        assertTrue(screens.get(3).contains("Pizza Roma"));
    }

    @Test
    void twoBlankCriteriaRestoreAllRestaurants()
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(CATALOG);
        restaurants.filterResponses.add(List.of(TURKISH));

        String output = run("1\n1\nTur\nKad\n1\n \t\n\u2003\n0\n0\n",
                new ActiveCustomerService(), restaurants);

        assertEquals(List.of(new FilterQuery("Tur", "Kad")), restaurants.queries);
        assertEquals(2, restaurants.allCalls);
        String lastScreen = screens(output).getLast();
        assertTrue(lastScreen.contains("Cuisine: All | District: All"));
        assertTrue(lastScreen.contains("Pizza Roma"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1\n 0 \n", "1\nIta\n 0 \n"})
    void cancellingEitherFilterPromptKeepsPreviousCriteriaAndResults(String cancelledAttempt)
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(CATALOG);
        restaurants.filterResponses.add(List.of(TURKISH));

        String output = run("1\n1\nTur\nKad\n" + cancelledAttempt + "0\n0\n",
                new ActiveCustomerService(), restaurants);

        assertEquals(List.of(new FilterQuery("Tur", "Kad")), restaurants.queries);
        assertEquals(1, restaurants.allCalls);
        String lastScreen = screens(output).getLast();
        assertTrue(lastScreen.contains("Cuisine: Tur | District: Kad"));
        assertTrue(lastScreen.contains("Kebapçı Mehmet"));
        assertFalse(lastScreen.contains("Pizza Roma"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"1\n", "1\n1\n", "1\n1\nTur\n"})
    void eofAtListOrEitherFilterPromptClosesNormallyWithoutApplyingPartialFilters(String script)
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(CATALOG);
        ActiveCustomerService customers = new ActiveCustomerService();

        String output = run(script, customers, restaurants);

        assertEquals(1, occurrences(output, "Goodbye."));
        assertEquals(1, restaurants.allCalls);
        assertTrue(restaurants.queries.isEmpty());
        assertEquals(0, customers.logoutCalls);
        assertTrue(customers.getActiveCustomer().isPresent());
    }

    @Test
    void noMatchesKeepsFilterActionsUsableAndCanBeCleared()
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(CATALOG);
        restaurants.filterResponses.add(List.of());

        String output = run("1\n1\nRamen\nShibuya\n2\n0\n0\n",
                new ActiveCustomerService(), restaurants);

        String noMatches = screens(output).get(1);
        assertTrue(noMatches.contains("Cuisine: Ramen | District: Shibuya"));
        assertTrue(noMatches.contains("No restaurants match the selected filters."));
        assertFalse(noMatches.contains("No restaurants are available."));
        assertTrue(noMatches.contains("1. Filter restaurants"));
        assertTrue(noMatches.contains("2. Clear filters"));
        assertTrue(noMatches.contains("0. Back"));
        assertTrue(screens(output).getLast().contains("Kebapçı Mehmet"));
    }

    @Test
    void reenteringBrowseResetsTheFiltersAndQueriesTheWholeCatalog()
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(CATALOG);
        restaurants.filterResponses.add(List.of(TURKISH));

        String output = run("1\n1\nTur\nKad\n0\n1\n0\n0\n",
                new ActiveCustomerService(), restaurants);

        assertEquals(2, restaurants.allCalls);
        String lastScreen = screens(output).getLast();
        assertTrue(lastScreen.contains("Cuisine: All | District: All"));
        assertTrue(lastScreen.contains("Pizza Roma"));
        assertEquals(3, occurrences(output, "Customer: Alice"));
    }

    @Test
    void invalidListChoiceRetriesWithoutReloadingOrChangingCustomer()
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(CATALOG);
        ActiveCustomerService customers = new ActiveCustomerService();

        String output = run("1\n9\n0\n0\n", customers, restaurants);

        assertTrue(output.contains("Please enter one of the available options."));
        assertEquals(1, restaurants.allCalls);
        assertEquals(0, customers.logoutCalls);
    }

    @Test
    @ResourceLock("java.util.Locale")
    void menuGroupsItemsByCategoryAndPrintsEveryPriceWithTwoDecimalsAndTl()
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(CATALOG);
        restaurants.menus.put("R01", TURKISH_MENU);
        Locale original = Locale.getDefault();
        String output;
        try
        {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            output = run("1\n3\nR01\n0\n0\n", new ActiveCustomerService(), restaurants);
        }
        finally
        {
            Locale.setDefault(original);
        }

        assertTrue(output.contains("3. View a menu"));
        assertTrue(output.contains("Menu - Kebapçı Mehmet"));
        assertTrue(output.contains("Lahmacun"));
        assertTrue(output.contains("140.00 TL"));
        assertTrue(output.contains("40.00 TL"));
        assertTrue(output.contains("Delivery fee: 25.00 TL"));
        assertFalse(output.contains("140,00 TL"));
        // The two Main items share one heading, which is printed before the Drink heading.
        assertEquals(1, occurrences(output, "\nMain\n"));
        assertEquals(1, occurrences(output, "\nDrink\n"));
        assertTrue(output.indexOf("Adana Kebap") < output.indexOf("\nDrink\n"));
    }

    @Test
    void unknownRestaurantIdIsReportedAndTheCustomerCanViewAMenuAfterwards()
    {
        RecordingRestaurantService restaurants = new RecordingRestaurantService(CATALOG);
        restaurants.menus.put("R01", TURKISH_MENU);

        String output = run("1\n3\nR99\n3\nR01\n0\n0\n",
                new ActiveCustomerService(), restaurants);

        assertTrue(output.contains("Error: No restaurant has the ID R99."));
        assertTrue(output.contains("Menu - Kebapçı Mehmet"));
        assertEquals(1, occurrences(output, "Goodbye."));
    }

    private String run(String script, ICustomerService customers, IRestaurantService restaurants)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (Scanner scanner = new Scanner(script);
             PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8))
        {
            new ConsoleUI(customers, restaurants,
                    new OrderService(new InMemoryCustomerRepository(), new InMemoryOrderRepository()),
                    new ConsoleInput(scanner, output), output).run();
        }
        return bytes.toString(StandardCharsets.UTF_8).replace("\r\n", "\n");
    }

    private static List<String> screens(String output)
    {
        List<String> screens = new ArrayList<>();
        String[] parts = output.split("\nRestaurants\n", -1);
        for (int index = 1; index < parts.length; index++)
        {
            screens.add(parts[index]);
        }
        return screens;
    }

    private static int occurrences(String text, String part)
    {
        return text.split(java.util.regex.Pattern.quote(part), -1).length - 1;
    }

    private record FilterQuery(String cuisine, String district) { }

    private static final class RecordingRestaurantService implements IRestaurantService
    {
        private final List<RestaurantDto> catalog;
        private final Queue<List<RestaurantDto>> filterResponses = new ArrayDeque<>();
        private final List<FilterQuery> queries = new ArrayList<>();
        private final Map<String, MenuDto> menus = new LinkedHashMap<>();
        private int allCalls;

        private RecordingRestaurantService(List<RestaurantDto> catalog)
        {
            this.catalog = catalog;
        }

        @Override
        public ArrayList<RestaurantDto> getAllRestaurants()
        {
            allCalls++;
            return new ArrayList<>(catalog);
        }

        @Override
        public ArrayList<RestaurantDto> filterRestaurants(String cuisine, String district)
        {
            queries.add(new FilterQuery(cuisine, district));
            return new ArrayList<>(filterResponses.remove());
        }

        @Override
        public RestaurantDto getRestaurantById(String id)
        {
            throw new AssertionError("Restaurant selection is outside this screen.");
        }

        @Override
        public MenuDto viewMenu(String restaurantId)
        {
            MenuDto menu = menus.get(restaurantId.strip());
            if (menu == null)
            {
                throw new IllegalArgumentException("No restaurant has the ID " + restaurantId.strip() + ".");
            }
            return menu;
        }
    }

    private static final class ActiveCustomerService implements ICustomerService
    {
        private CustomerDto active = new CustomerDto("Alice", "alice@example.com", "Kadıköy", "Moda Street");
        private int logoutCalls;

        @Override
        public void validateRegistrationEmail(String email)
        {
            throw new AssertionError("Registration is not part of this fixture.");
        }

        @Override
        public CustomerDto registerCustomer(RegisterCustomerDto request)
        {
            throw new AssertionError("Registration is not part of this fixture.");
        }

        @Override
        public CustomerDto loginExisting(String email)
        {
            throw new AssertionError("Login is not part of this fixture.");
        }

        @Override
        public Optional<CustomerDto> getActiveCustomer()
        {
            return Optional.ofNullable(active);
        }

        @Override
        public void logout()
        {
            logoutCalls++;
            active = null;
        }
    }
}
