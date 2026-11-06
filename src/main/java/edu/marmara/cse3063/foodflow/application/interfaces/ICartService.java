package edu.marmara.cse3063.foodflow.application.interfaces;

import edu.marmara.cse3063.foodflow.domain.models.Cart;
import edu.marmara.cse3063.foodflow.domain.models.MenuItem;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;

public interface ICartService {
    Cart addItem(String itemId, String restaurantId, Cart cart, int quantity) throws Exception;
    Cart removeItem(String itemId, Cart cart) throws Exception;
    Cart changeQuantity(String itemId, Cart cart, int newQuantity) throws Exception;
    Cart viewCart(Cart cart);
}