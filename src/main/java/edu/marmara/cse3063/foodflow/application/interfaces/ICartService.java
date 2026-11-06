package edu.marmara.cse3063.foodflow.application.interfaces;

import edu.marmara.cse3063.foodflow.application.dtos.CartDto;
import edu.marmara.cse3063.foodflow.domain.models.Cart;

public interface ICartService {
    CartDto addItem(String itemId, String restaurantId, Cart cart, int quantity) throws Exception;
    CartDto removeItem(String itemId, Cart cart) throws Exception;
    CartDto changeQuantity(String itemId, Cart cart, int newQuantity) throws Exception;
    CartDto viewCart(Cart cart) throws Exception;
}