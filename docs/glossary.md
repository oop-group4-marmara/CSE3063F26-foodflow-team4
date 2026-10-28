# Glossary



Terms for FoodFlow. Use them everywhere.

Aliases explain common alternative names.


| Term           | Definition                                                                                                                                        | Notes / rules (BR-x)   | Aliases              |
| -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------- | ---------------------- | -------------------- |
| Address        | The district and street where the customer wants an order delivered.                                                                              | BR-4                   |                      |
| Cart           | The customer’s current selection of menu items with quantities, from a single restaurant.                                                       | BR-2, BR-3, BR-4, BR-7 | Basket               |
| Cart line      | One menu item and its quantity in a cart.                                                                                                         | BR-1, BR-3             | Cart entry           |
| Category       | A group of menu items within a restaurant's menu.                                                                                                 |                        |                      |
| Courier        | A person who delivers orders; has a vehicle, a location and an availability.                                                                      |                        | Delivery person      |
| Cuisine        | The type of food a restaurant offers.                                                                                                             |                        |                      |
| Customer       | A person who orders food through FoodFlow; identified by a unique e-mail address.                                                                 | BR-8                   |                      |
| Delivery fee   | The amount charged for delivery; flat per restaurant in I1, distance-based from I2.                                                               | BR-5                   | Delivery charge      |
| District       | The area of the city where a restaurant or delivery address is located, such as Kadıköy.                                                          |                        |                      |
| E-mail address | The customer's unique identifier in FoodFlow; it must contain `@`.                                                                              | BR-8                   | Email                |
| Invalid input  | An entered value or command that does not meet the system's requirements.                                                                         | BR-9                   |                      |
| Line total     | The unit price multiplied by quantity for one cart line or order line; order lines use the price recorded at placement.                         | BR-5, BR-7             |                      |
| Menu           | The collection of menu items offered by a restaurant.                                                                                             |                        |                      |
| Menu item      | Something a restaurant sells, with a name, a category and a price.                                                                                | BR-1, BR-7             | Dish                 |
| Minimum basket | The minimum basket amount required to place an order with a restaurant, applicable from I2.                                                     |                        | Minimum order amount |
| Order          | A confirmed request to deliver the contents of a cart to an address; has a number, a status and a total.                                          | BR-4, BR-5, BR-6, BR-7 |                      |
| Order line     | A record of an ordered menu item's name, unit price and quantity at the time of placement.                                                        | BR-7                   | Order entry          |
| Order number   | A unique, increasing number assigned to an order when it is placed.                                                                             | BR-6                   | Order ID             |
| Order status   | Where an order is in its life cycle: placed, accepted, preparing, ready, picked up, delivered, rejected or cancelled (grows over the iterations). | BR-6                   |                      |
| Payment        | The settlement of an order: cash in I1; card, wallet and an external gateway from I3.                                                             |                        |                      |
| Payment Method | The way an order is paid for; cash on delivery is the only method in I1.                                                                          |                        |                      |
| Promotion      | A rule that reduces the price of an order or of items: coupons, campaigns, free delivery (I3).                                                    |                        | Discount offer       |
| Quantity       | The whole number of units of a menu item in a cart line or order line; cart line quantities range from 1 to 20.                                 | BR-1, BR-3             | Item count           |
| Receipt        | The confirmation printed after an order is successfully placed.                                                                                   |                        | Order confirmation   |
| Restaurant     | A business that offers a menu, has a cuisine, a district, a delivery fee and (from I2) opening hours and a minimum basket.                        |                        |                      |
| Subtotal       | The sum of all line totals in a cart or order, before the delivery fee.                                                                           | BR-5                   | Food subtotal        |
| Total          | The full amount the customer owes for the cart or order: subtotal plus delivery fee.                                                              | BR-5, BR-7             |                      |

## Naming conventions

Join multiword terms when writing Java identifiers, for example `MenuItem` and `OrderLine`.

- **Classes and enums:** Use singular PascalCase names, such as Customer, not Customers.
- **Methods:** Use camelCase names starting with a verb, such as addItem and placeOrder.
- **Fields, parameters and variables:** Use descriptive camelCase names, such as quantity.
- **Lists:** Use plural camelCase names, such as menuItems.
- **Constants:** Use UPPER_SNAKE_CASE names, such as MAX_QUANTITY and PLACED.
- **Packages:** Use lowercase names under edu.marmara.cse3063.foodflow, such as domain, application, infrastructure and ui.
- **Java files:** Match the public class or enum name exactly, such as MenuItem.java.
