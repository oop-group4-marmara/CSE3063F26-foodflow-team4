package edu.marmara.cse3063.foodflow.application.interfaces;

import edu.marmara.cse3063.foodflow.application.dtos.OrderDto;
import edu.marmara.cse3063.foodflow.application.dtos.OrderPreviewDto;

/** UC6 Place Order. */
public interface IOrderService
{
    /** Checks BR-4 and returns the lines and amounts for confirmation without placing the order. */
    OrderPreviewDto previewOrder(String customerEmail);

    /** Places the customer's cart as a cash-on-delivery order, empties the cart and returns the receipt. */
    OrderDto placeOrder(String customerEmail);
}
