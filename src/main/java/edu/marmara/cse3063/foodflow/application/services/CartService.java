package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.dtos.CartDto;
import edu.marmara.cse3063.foodflow.application.dtos.CartLineDto;
import edu.marmara.cse3063.foodflow.application.interfaces.ICartService;
import edu.marmara.cse3063.foodflow.domain.models.Cart;
import edu.marmara.cse3063.foodflow.domain.models.CartLine;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import edu.marmara.cse3063.foodflow.application.repositories.ICustomerRepository;
import edu.marmara.cse3063.foodflow.application.repositories.IRestaurantRepository;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class CartService implements ICartService {
    private final IRestaurantRepository restaurantRepository;
    private final ICustomerRepository customerRepository;

    //Mr. Yaman quoted: "Any service can access any repository"
    public CartService(IRestaurantRepository restaurantRepository, ICustomerRepository customerRepository) {
        this.restaurantRepository = Objects.requireNonNull(restaurantRepository, "restaurantRepository");
        this.customerRepository = Objects.requireNonNull(customerRepository, "customerRepository");
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

    //an empty cart belongs to no restaurant, so its total must not include a delivery fee
    private void deleteLine(Cart cart, CartLine line) {
        cart.deleteCartLine(line);
        if (cart.getLines().isEmpty()) cart.emptyCart();
    }

    private CartDto toDto(Cart cart) {
        ArrayList<CartLineDto> dtoList = new ArrayList<>();
        for (CartLine line : cart.getLines()) {
            dtoList.add(new CartLineDto(
                    line.getMenuItem().getId(),
                    line.getMenuItem().getName(),
                    line.getMenuItem().getPrice(),
                    line.getQuantity(),
                    line.getLineTotal()
            ));
        }
        double deliveryFee = cart.getRestaurant() == null ? 0.0 : cart.getRestaurant().getDeliveryFee();
        String restaurantName = cart.getRestaurant() == null ? null : cart.getRestaurant().getName();
        return new CartDto(restaurantName, dtoList, cart.getSubtotal(), deliveryFee, cart.getTotal());
    }

    @Override
    public CartDto addItem(String itemId, String restaurantId, Cart cart, int quantity) {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("Item Id can't be null!");
        if (restaurantId == null || restaurantId.isBlank()) throw new IllegalArgumentException("Restaurant Id can't be null!");

        Restaurant restaurant = restaurantRepository.getRestaurantById(restaurantId);
        if (restaurant == null) throw new IllegalArgumentException("Restaurant doesn't exist!");


        //checks if the given itemId is served by the restaurant
        MenuItem item = null;
        for (MenuItem i : restaurant.getMenuItems()) {
            if (i.getId().equals(itemId)) {
                item = i;
                break;
            }
        }
        if (item == null) throw new IllegalArgumentException("Menu Item not found!");


        if (quantity < 1 || quantity > 20) throw new IllegalArgumentException("Quantity must be between 1 and 20.");

        if (cart.getLines().isEmpty()) cart.setRestaurant(restaurant);
        if (!cart.getRestaurant().getId().equals(restaurant.getId())) throw new IllegalStateException("Adding an item from another restaurant is refused. Please empty the cart to continue.");

        //the addItem function
        CartLine line = findLine(cart, itemId);
        if (line != null) {
            int currentQuantity = line.getQuantity();
            int newQuantity = currentQuantity + quantity;

            if (newQuantity > 20) throw new IllegalArgumentException("Cannot add item. Total quantity would exceed the maximum limit of 20.");
            line.setQuantity(newQuantity);
        }

        else {
            ArrayList<CartLine> currentLines = cart.getLines();
            currentLines.add(new CartLine(item, quantity));
            cart.setLines(currentLines);
        }

        return toDto(cart);
    }

    @Override
    public CartDto changeQuantity(String itemId, Cart cart, int newQuantity) {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("Item Id can't be null!");

        if (newQuantity > 20) throw new IllegalArgumentException("Quantity can't be higher than 20!");
        if (newQuantity < 0) throw new IllegalArgumentException("Quantity can't be lower than 0!");


        CartLine line = findLine(cart, itemId);
        if (line == null) throw new IllegalArgumentException("Item not found in cart!");

        if (newQuantity == 0) deleteLine(cart, line);
        else line.setQuantity(newQuantity);

        return toDto(cart);
    }

    @Override
    public CartDto removeItem(String itemId, Cart cart) {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");
        if (itemId == null || itemId.isBlank()) throw new IllegalArgumentException("Item Id can't be null!");

        CartLine line = findLine(cart, itemId);
        if (line == null) throw new IllegalArgumentException("Item not found in cart!");

        int currentQuantity = line.getQuantity();
        if (currentQuantity <= 1) {
            deleteLine(cart, line);
        }

        else line.setQuantity(currentQuantity - 1);

        return toDto(cart);
    }

    @Override
    public CartDto viewCart(Cart cart) {
        if (cart == null) throw new IllegalArgumentException("Cart can't be null!");

        if (cart.getLines().isEmpty()) throw new IllegalStateException("The cart is empty.");

        return toDto(cart);
    }

    @Override
    public CartDto addToCart(String customerEmail, String restaurantId, String itemId, int quantity) {
        Cart cart = findCart(customerEmail);
        return addItem(strip(itemId), strip(restaurantId), cart, quantity);
    }

    @Override
    public CartDto changeCartQuantity(String customerEmail, String itemId, int newQuantity) {
        Cart cart = findCart(customerEmail);
        return changeQuantity(strip(itemId), cart, newQuantity);
    }

    @Override
    public CartDto removeFromCart(String customerEmail, String itemId) {
        return changeCartQuantity(customerEmail, itemId, 0);
    }

    @Override
    public CartDto emptyCart(String customerEmail) {
        Cart cart = findCart(customerEmail);
        cart.emptyCart();
        return toDto(cart);
    }

    @Override
    public CartDto getCart(String customerEmail) {
        return toDto(findCart(customerEmail));
    }

    private Cart findCart(String customerEmail) {
        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException("A customer must be identified before using the cart.");
        }
        Customer customer = customerRepository.findCustomerByEmail(customerEmail)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Customer with e-mail '" + customerEmail.trim().toLowerCase(Locale.ROOT) + "' not found."));
        return customer.getCart();
    }

    private static String strip(String value) {
        return value == null ? null : value.strip();
    }
}
