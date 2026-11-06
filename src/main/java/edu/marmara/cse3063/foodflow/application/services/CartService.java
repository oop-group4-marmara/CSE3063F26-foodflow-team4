package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.interfaces.ICartService;
import edu.marmara.cse3063.foodflow.domain.models.Cart;
import edu.marmara.cse3063.foodflow.domain.models.CartLine;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.IRestaurantRepository;

import java.util.ArrayList;
import java.util.Objects;

public class CartService implements ICartService {
    private final IRestaurantRepository restaurantRepository;

    //Mr. Yaman quoted: "Any service can access any repository"
    public CartService(IRestaurantRepository restaurantRepository) {
        this.restaurantRepository = Objects.requireNonNull(restaurantRepository, "restaurantRepository");
    }

    //finds if a menu item is included already in a cart line
    private CartLine findLine(Cart cart, String itemId) {
        for (CartLine line : cart.getLines()) {
            if (line.getMenuItem().getId().equals(itemId)) {
                return line;
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


        //checks if the given itemId is served by the restaurant
        MenuItem item = null;
        for (MenuItem i : restaurant.getMenuItems()) {
            if (i.getId().equals(itemId)) {
                item = i;
                break;
            }
        }
        if (item == null) throw new Exception("Menu Item not found!");


        if (quantity < 1 || quantity > 20) throw new IllegalArgumentException("Quantity must be between 1 and 20.");

        if (cart.getLines().isEmpty()) cart.setRestaurant(restaurant);
        if (!cart.getRestaurant().getId().equals(restaurant.getId())) throw new Exception("Adding an item from another restaurant is refused. Please empty the cart to continue.");

        //the addItem function
        CartLine line = findLine(cart, itemId);
        if (line != null) {
            int currentQuantity = line.getQuantity();
            int newQuantity = currentQuantity + quantity;

            if (newQuantity > 20) throw new Exception("Cannot add item. Total quantity would exceed the maximum limit of 20.");
            line.setQuantity(newQuantity);
        }

        else {
            ArrayList<CartLine> currentLines = cart.getLines();
            currentLines.add(new CartLine(item, quantity));
            cart.setLines(currentLines);
        }

        return cart;
    }

    @Override
    public Cart changeQuantity(String itemId, Cart cart, int newQuantity) throws Exception {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("Item Id can't be null!");

        if (newQuantity > 20) throw new IllegalArgumentException("Quantity can't be higher than 20!");
        if (newQuantity < 0) throw new IllegalArgumentException("Quantity can't be lower than 0!");


        CartLine line = findLine(cart, itemId);

        if (newQuantity == 0) {
            if (line != null) cart.deleteCartLine(line);
        }

        else {
            if (line != null) line.setQuantity(newQuantity);
            else throw new Exception("Item not found in cart!");
        }

        return cart;
    }

    @Override
    public Cart removeItem(String itemId, Cart cart) throws Exception {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("Item Id can't be null!");

        CartLine line = findLine(cart, itemId);
        if (line == null) throw new Exception("Item not found in cart!");

        int currentQuantity = line.getQuantity();
        if (currentQuantity <= 1) {
            cart.deleteCartLine(line);
        }

        else line.setQuantity(currentQuantity - 1);

        return cart;
    }

    @Override
    public String viewCart(Cart cart) throws Exception {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");

        if (cart.getLines().isEmpty()) throw new Exception("The cart is empty.");

        StringBuilder s = new StringBuilder();

        for (CartLine line : cart.getLines()) {
            s.append(String.format("Item: %s | Price: %.2f TL | Quantity: %d | Line Total: %.2f TL\n",
                    line.getMenuItem().getName(),
                    line.getMenuItem().getPrice(),
                    line.getQuantity(),
                    line.getLineTotal()));
        }

        s.append(String.format("Subtotal: %.2f TL\n", cart.getSubtotal()));
        s.append(String.format("Delivery Fee: %.2f TL\n", cart.getRestaurant().getDeliveryFee()));
        s.append(String.format("Total: %.2f TL", cart.getTotal()));

        return s.toString();
    }
}