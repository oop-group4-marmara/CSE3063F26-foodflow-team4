package edu.marmara.cse3063.foodflow.application.interfaces;

import edu.marmara.cse3063.foodflow.application.dtos.CartDto;
import edu.marmara.cse3063.foodflow.domain.models.Cart;

public interface ICartService {
    CartDto addItem(String itemId, String restaurantId, Cart cart, int quantity);
    CartDto removeItem(String itemId, Cart cart);
    CartDto changeQuantity(String itemId, Cart cart, int newQuantity);
    CartDto viewCart(Cart cart);

    // UC5 for the UI: the customer's cart is found by e-mail, so the UI never handles the Domain Cart.
    // Refused requests are reported with IllegalArgumentException or IllegalStateException (BR-9).
    CartDto addToCart(String customerEmail, String restaurantId, String itemId, int quantity);
    CartDto changeCartQuantity(String customerEmail, String itemId, int newQuantity);
    /** Removes the whole cart line, whatever its quantity. */
    CartDto removeFromCart(String customerEmail, String itemId);
    CartDto emptyCart(String customerEmail);
    /** Returns the cart, which may be empty. */
    CartDto getCart(String customerEmail);
}
