package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.interfaces.ICartService;
import edu.marmara.cse3063.foodflow.domain.models.Cart;
import edu.marmara.cse3063.foodflow.domain.models.CartLine;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IRestaurantRepository;

public class CartService implements ICartService {

    private final IRestaurantRepository restaurantRepository;

    public CartService(IRestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    private MenuItem findLine(Cart cart, String itemId) {
        for (CartLine line : cart.getLines()) {
            if (line.getMenuItem().getId().equals(itemId)) {
                return line.getMenuItem();
            }
        }
        return null;
    }

    @Override
    public Cart addItem(String itemId, String restaurantId, Cart cart, int quantity) throws Exception {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("Item Id can't be null!");
        if (restaurantId == null || restaurantId.isBlank()) throw new IllegalArgumentException("RestaurantvId can't be null!");

        Restaurant restaurant = restaurantRepository.getRestaurantById(restaurantId);
        if (restaurant == null) throw new Exception("Restaurant doesn't exist!");

        //checks if the restaurant has the provided item
        MenuItem item = null;
        for (MenuItem i : restaurant.getMenuItems()) {
            if (i.getId().equals(itemId)) {
                item = i;
                break;
            }
        }

        if (item == null) throw new Exception("Menu Item not found!");

        cart.addItem(item, quantity, restaurant);

        return cart;
    }

    @Override
    public Cart changeQuantity(String itemId, Cart cart, int newQuantity) throws Exception {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("Item Id can't be null!");

        MenuItem item = findLine(cart, itemId);
        if (item == null) throw new Exception("Item not found in cart!");

        cart.changeQuantity(item, newQuantity);

        return cart;
    }

    @Override
    public Cart removeItem(String itemId, Cart cart) throws Exception {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("Item Id can't be null!");

        MenuItem item = findLine(cart, itemId);
        if (item == null) throw new Exception("Item not found in cart!");

        cart.removeItem(item);

        return cart;
    }

    @Override
    public Cart viewCart(Cart cart) {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");
        return cart;
    }
}