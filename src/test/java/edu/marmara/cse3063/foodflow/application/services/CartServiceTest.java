package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.dtos.CartDto;
import edu.marmara.cse3063.foodflow.domain.models.Cart;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IRestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CartServiceTest {

    private Cart cart;
    private CartService cartService;
    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private MenuItem item1;
    private MenuItem item2;
    private MenuItem item3;

    @BeforeEach
    void setUp() throws Exception {
        cart = new Cart();

        item1 = new MenuItem("R01-01", "Lahmacun", "Main", 140.0);
        item2 = new MenuItem("R01-02", "Adana Kebap", "Main", 320.0);
        restaurant1 = new Restaurant("R01", "Kebapçı Mehmet", "Turkish", null, "Kadıköy", 25.0, new ArrayList<>(List.of(item1, item2)));

        item3 = new MenuItem("R02-01", "Margherita", "Pizza", 320.0);
        restaurant2 = new Restaurant("R02", "Pizza Roma", "Italian", null, "Beşiktaş", 40.0, new ArrayList<>(List.of(item3)));


        //have to override these functions since the cart will use them, for testing purposes
        IRestaurantRepository r = new IRestaurantRepository() {
            @Override
            public ArrayList<Restaurant> getAllRestaurants() {
                return new ArrayList<>(List.of(restaurant1, restaurant2));
            }

            @Override
            public Restaurant getRestaurantById(String id) {
                if ("R01".equals(id)) return restaurant1;
                if ("R02".equals(id)) return restaurant2;
                return null;
            }
        };

        cartService = new CartService(r);
    }

    // BR-1
    @Test
    void testAddItemQuantity() throws Exception {
        CartDto result = cartService.addItem("R01-01", "R01", cart, 2);

        assertEquals(1, result.getLines().size());
        assertEquals(2, result.getLines().getFirst().getQuantity());
        assertEquals(25.0, result.getDeliveryFee());
    }

    // BR-1
    @Test
    void testAddItemQuantityBelowMinimum() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem("R01-01", "R01", cart, 0));
    }

    // BR-1
    @Test
    void testAddItemQuantityAboveMaximum() {
        assertThrows(IllegalArgumentException.class, () -> cartService.addItem("R01-01", "R01", cart, 21));
    }

    // BR-3
    @Test
    void testAddItemAlreadyInCartIncreasesQuantity() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 5);
        CartDto result = cartService.addItem("R01-01", "R01", cart, 3);

        assertEquals(1, result.getLines().size());
        assertEquals(8, result.getLines().getFirst().getQuantity());
    }

    // BR-1
    @Test
    void testAddItemAlreadyInCartIncreasesQuantityAboveMaximum() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 15);

        Exception exception = assertThrows(Exception.class, () -> cartService.addItem("R01-01", "R01", cart, 6));
        assertTrue(exception.getMessage().contains("Cannot add item. Total quantity would exceed the maximum limit of 20."));
    }

    // BR-2
    @Test
    void testAddItemFromDifferentRestaurant() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 1);

        Exception exception = assertThrows(Exception.class, () -> cartService.addItem("R02-01", "R02", cart, 1));
        assertTrue(exception.getMessage().contains("Adding an item from another restaurant is refused. Please empty the cart to continue."));
    }

    // BR-1
    @Test
    void testRemoveItemDecreasesQuantity() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 3);
        CartDto result = cartService.removeItem("R01-01", cart);

        assertEquals(2, result.getLines().getFirst().getQuantity());
    }

    // BR-1 & BR-3
    @Test
    void testRemoveItemDeletesLineIfQuantityIsOne() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 1);
        CartDto result = cartService.removeItem("R01-01", cart);

        assertTrue(result.getLines().isEmpty());
        assertNull(cart.getRestaurant());
        assertEquals(0.0, result.getDeliveryFee());
        assertEquals(0.0, result.getTotal());
    }

    // BR-1
    @Test
    void testChangeItemQuantity() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 5);
        CartDto result = cartService.changeQuantity("R01-01", cart, 12);

        assertEquals(12, result.getLines().getFirst().getQuantity());
    }

    // BR-1
    @Test
    void testChangeItemQuantityToZeroRemovesLine() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 5);
        CartDto result = cartService.changeQuantity("R01-01", cart, 0);

        assertTrue(result.getLines().isEmpty());
        assertNull(cart.getRestaurant());
        assertEquals(0.0, result.getTotal());
    }

    // BR-2
    @Test
    void testEmptiedCartAcceptsAnotherRestaurant() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 1);
        cartService.removeItem("R01-01", cart);
        CartDto result = cartService.addItem("R02-01", "R02", cart, 1);

        assertEquals("Margherita", result.getLines().getFirst().getItemName());
        assertEquals(40.0, result.getDeliveryFee());
    }

    @Test
    void testChangeQuantityOfMissingItemIsRefused() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 1);

        assertThrows(Exception.class, () -> cartService.changeQuantity("R01-02", cart, 0));
        assertThrows(Exception.class, () -> cartService.changeQuantity("R01-02", cart, 3));
        assertEquals(1, cart.getLines().size());
    }

    // BR-1
    @Test
    void testChangeItemQuantityOutsideRange() {
        assertThrows(IllegalArgumentException.class, () -> cartService.changeQuantity("R01-01", cart, 25));
        assertThrows(IllegalArgumentException.class, () -> cartService.changeQuantity("R01-01", cart, -5));
    }

    // BR-5
    @Test
    void testCartTotalsCalculation() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 2);
        CartDto result = cartService.addItem("R01-02", "R01", cart, 1);

        assertEquals(600.0, result.getSubtotal());
        assertEquals(625.0, result.getTotal());
    }

    // BR-2
    @Test
    void testEmptyCartClearsLines() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 2);
        cart.emptyCart();

        assertTrue(cart.getLines().isEmpty());
        assertNull(cart.getRestaurant());
    }

    // 8a: System reports that the cart is empty
    @Test
    void testViewEmptyCartThrowsException() {
        Exception exception = assertThrows(Exception.class, () -> cartService.viewCart(cart));
        assertEquals("The cart is empty.", exception.getMessage());
    }

    // BR-5
    @Test
    void testViewCartReturnsCorrectDetails() throws Exception {
        cartService.addItem("R01-01", "R01", cart, 2);

        CartDto output = cartService.viewCart(cart);

        assertEquals(1, output.getLines().size());
        assertEquals("Lahmacun", output.getLines().getFirst().getItemName());
        assertEquals(140.0, output.getLines().getFirst().getUnitPrice());
        assertEquals(2, output.getLines().getFirst().getQuantity());
        assertEquals(280.0, output.getLines().getFirst().getLineTotal());
        assertEquals(280.0, output.getSubtotal());
        assertEquals(25.0, output.getDeliveryFee());
        assertEquals(305.0, output.getTotal());
    }
}