package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.domain.models.Cart;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.domain.models.CartLine;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CartTest {

    private Cart cart;
    private Restaurant restaurant1;
    private Restaurant restaurant2;
    private MenuItem item1;
    private MenuItem item2;
    private MenuItem item3;

    @BeforeEach
    void setUp() throws Exception {
        cart = new Cart();

        restaurant1 = new Restaurant("R01", "Kebapçı Mehmet", "Turkish", null, "Kadıköy", 25.0);
        item1 = new MenuItem("R01-01", "Lahmacun", "Main", 140.0);
        item2 = new MenuItem("R01-02", "Adana Kebap", "Main", 320.0);

        restaurant2 = new Restaurant("R02", "Pizza Roma", "Italian", null, "Beşiktaş", 40.0);
        item3 = new MenuItem("R02-01", "Margherita", "Pizza", 320.0);
    }

    // BR-1
    @Test
    void testAddItemQuantity() throws Exception {
        cart.addItem(item1, 2, restaurant1);
        assertEquals(1, cart.getLines().size());
        assertEquals(2, cart.getLines().getFirst().getQuantity());
        assertEquals(restaurant1, cart.getRestaurant());
    }

    // BR-1
    @Test
    void testAddItemQuantityBelowMinimum() {
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(item1, 0, restaurant1));
    }

    // BR-1
    @Test
    void testAddItemQuantityAboveMaximum() {
        assertThrows(IllegalArgumentException.class, () -> cart.addItem(item1, 21, restaurant1));
    }

    // BR-3
    @Test
    void testAddItemAlreadyInCartIncreasesQuantity() throws Exception {
        cart.addItem(item1, 5, restaurant1);
        cart.addItem(item1, 3, restaurant1);

        assertEquals(1, cart.getLines().size());
        assertEquals(8, cart.getLines().getFirst().getQuantity());
    }

    // BR-1
    @Test
    void testAddItemAlreadyInCartIncreasesQuantityAboveMaximum() throws Exception {
        cart.addItem(item1, 15, restaurant1);

        Exception exception = assertThrows(Exception.class, () -> cart.addItem(item1, 6, restaurant1));
        assertTrue(exception.getMessage().contains("Cannot add item. Total quantity would exceed the maximum limit of 20."));
    }

    // BR-2
    @Test
    void testAddItemFromDifferentRestaurant() throws Exception {
        cart.addItem(item1, 1, restaurant1);

        Exception exception = assertThrows(Exception.class, () -> cart.addItem(item3, 1, restaurant2));
        assertTrue(exception.getMessage().contains("Adding an item from another restaurant is refused. Please empty the cart to continue."));
    }

    // BR-1
    @Test
    void testRemoveItemDecreasesQuantity() throws Exception {
        cart.addItem(item1, 3, restaurant1);
        cart.removeItem(item1);

        assertEquals(2, cart.getLines().get(0).getQuantity());
    }

    // BR-1 & BR-3
    @Test
    void testRemoveItemDeletesLineIfQuantityIsOne() throws Exception {
        cart.addItem(item1, 1, restaurant1);
        cart.removeItem(item1);

        assertTrue(cart.getLines().isEmpty());
    }

    // BR-1
    @Test
    void testChangeItemQuantity() throws Exception {
        cart.addItem(item1, 5, restaurant1);
        cart.changeQuantity(item1, 12);

        assertEquals(12, cart.getLines().getFirst().getQuantity());
    }

    // BR-1
    @Test
    void testChangeItemQuantityToZeroRemovesLine() throws Exception {
        cart.addItem(item1, 5, restaurant1);
        cart.changeQuantity(item1, 0);

        assertTrue(cart.getLines().isEmpty());
    }

    // BR-1
    @Test
    void testChangeItemQuantityOutsideRange() {
        assertThrows(IllegalArgumentException.class, () -> cart.changeQuantity(item1, 25));
        assertThrows(IllegalArgumentException.class, () -> cart.changeQuantity(item1, -5));
    }

    // BR-5
    @Test
    void testCartTotalsCalculation() throws Exception {
        cart.addItem(item1, 2, restaurant1);
        cart.addItem(item2, 1, restaurant1);

        assertEquals(600.0, cart.getSubtotal());
        assertEquals(625.0, cart.getTotal());
    }

    // BR-2
    @Test
    void testEmptyCartClearsLines() throws Exception {
        cart.addItem(item1, 2, restaurant1);
        cart.emptyCart();

        assertTrue(cart.getLines().isEmpty());
    }
}