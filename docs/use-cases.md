# Use-case model

Diagram: `diagrams/use-cases.puml`.

Operation contracts: `contracts.md`.

## Brief use cases
**UC1 Identify the Customer** — *The Customer wants to save their information so that orders reach them correctly. So the Customer registers with name, e-mail and address, and the system keeps it and makes them the current Customer. Next time the e-mail alone is enough.*

**UC2 Browse restaurants** — *The Customer wants to see all the Restaurants in FoodFlow. So the system lets the Customer browse every Restaurant with its name, cuisine, district and delivery fee.*

**UC3 Filter Restaurants** — *The Customer wants a specific kind of food or only the Restaurants in their own district. The Customer gives a cuisine, a district or both and the system shows the matching Restaurants.*

**UC4 View Menu** — *When the Customer has chosen a Restaurant, they want the Menu. So the system provides it. The Menu lists the items by category with their prices plus the delivery fee.*

**UC7 View Orders** — *The Customer asks for their previous Orders so the system lists each one with its number, Restaurant, total and status.*

**UC8 Check Order Status** — *After placing the order, the Customer wants to see the status of the Order. Whether it is still at the restaurant or in delivery progress. The system presents its current status and who is carrying it.*
                            
**UC15 Oversee Restaurants, Couriers and Orders** — *The Platform Administrator always wants information about how the system is running. So the system presents the Restaurants, the Couriers and the Orders with their statuses, making a stalled Restaurant or a late delivery visible.*

**UC16 Configure Fees and Promotions** — *Over time, the Platform Administrator will need to configure the fee and make promotions for Customers. The system applies the updated settings to all orders placed after the change.*

**UC17 Apply Promotions** — *After the Customer adds items to the Cart, the system displays the available promotions. When the Customer selects a promotion, the system applies it and updates the Cart total, which is calculated from the item prices plus the computed fees, minus the promotion discount.*

## Fully dressed use cases

### UC5 Manage Cart

| Section                            | Content                                                                                                                                                                                                                                                                                                                                                                                                                         |
|------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Scope                              | FoodFlow system                                                                                                                                                                                                                                                                                                                                                                                                                 |
| Level                              | User goal                                                                                                                                                                                                                                                                                                                                                                                                                       |
| Primary actor                      | Customer                                                                                                                                                                                                                                                                                                                                                                                                                        |
| Stakeholders and interests         | **Customer:** checks the cart most of the time, adding and removing items, and wants to see the quantity next to the price, plus the delivery fee, as the total. **Restaurant:** wants to receive orders only from its own menu and not mixed with other menus; this is a business rule. **Platform administrator:** wants the cart to stay stable and consistent, so orders are priced correctly and passed on to Place Order. |
| Preconditions                      | UC1: a Customer is identified. UC4: the Customer is at a Restaurant's menu.                                                                                                                                                                                                                                                                                                                                                     |
| Success guarantee (postconditions) | BR-1: the menu item price is positive and the quantity is between 1 and 20.<br>BR-2: all lines come from one Restaurant only.<br>BR-3: there are never two lines for the same menu item.<br>BR-5: the subtotal comes from the lines, and subtotal plus delivery fee is shown as the total.                                                                                                                                      |
| Trigger                            | The Customer wants to add, change or remove items before ordering.                                                                                                                                                                                                                                                                                                                                                              |

**Main success scenario**

1. Customer wants to add a menu item with a quantity.
2. System checks that the quantity is within the allowed range and adds a cart line.
3. Customer asks to change the quantity.
4. System checks that the new quantity is within the allowed range, then changes it. (Repeat step 3 and 4 until quantity is same with Customer preference)
5. Customer wants to remove a cart line.
6. System removes the line from the cart.
7. Customer wants to view the cart.
8. System presents the item name, its price, the quantity and the line total for each cart line.

**Extensions**

- \*a. At any time, Customer enters an unknown command. System reports a message to the Customer. (BR-9)
- 2a. The quantity is not in the allowed range of 1 to 20. System refuses the addition and reports that the quantity must be between 1 and 20. (BR-1)
- 2b. Customer adds an item already in a line. System increases the quantity of the same line. (BR-3)
- 2b1. The increase might exceed 20, so the System refuses the addition. (BR-1)
- 2c. Customer adds an item from a different Restaurant's menu. System refuses the request and suggests emptying the cart to continue. (BR-2)
- 2c1. Customer empties the cart. System clears all the lines.
- 2c2. Customer keeps the cart. System changes nothing.
- 4a. The new quantity is outside the range 1 to 20. System refuses the change. (BR-1)
- 6a. The cart has no line for the item the Customer wants. System reports that the item is not in the cart.
- 8a. The cart is empty. System reports that the cart is empty.

**Special requirements**

- Prices must be shown with two decimals and with the suffix "TL". (BR-5)
- The system never terminates; it always reports to the Customer with a message saying what was wrong. (BR-9)

**Technology and data variations**

- The item and the quantity are typed as identifiers.
- All information about the Restaurants, their menus and their prices comes from the JSON file (`restaurants.json`).
- The cart is shown as a console table; the fee comes from the Restaurant record.

**Frequency of occurrence**

Very frequent: several times in every ordering session, and the most used use case of this iteration.

**Open issues**

- Should "empty the cart" be a step of this use case, or a dependent use case of its own?
- Should a quantity of 0 be accepted as removing the line, or refused by BR-1?


### UC6 Place Order

| Section                            | Content                                                                                                                                                                                                                                                                                 |
|------------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Scope                              | FoodFlow system                                                                                                                                                                                                                                                                         |
| Level                              | User goal                                                                                                                                                                                                                                                                               |
| Primary actor                      | Customer                                                                                                                                                                                                                                                                                |
| Stakeholders and interests         | **Customer:** wants the order to reach their address, and wants proof of it in a receipt. **Restaurant:** wants a real and complete order with the items. **Courier:** wants the delivery address. **Platform administrator:** wants every order to be unique and traceable.            |
| Preconditions                      | UC1: a Customer is identified. UC5: the cart is not empty.                                                                                                                                                                                                                              |
| Success guarantee (postconditions) | BR-6: an order with a unique number and the status PLACED.<br>BR-5: the total is subtotal plus delivery fee.<br>BR-7: the receipt is reported and the cart is empty; the order keeps the item names and prices recorded at placement, so a later menu change does not change its total. |
| Trigger                            | The Customer asks to place the order for the current cart.                                                                                                                                                                                                                              |

**Main success scenario**

1. Customer asks to place the order.
2. System checks that the Customer has a delivery address and that the cart is not empty.
3. System constructs the total by adding the subtotal and the delivery fee, and presents it in detail.
4. System assigns a number to the order and sets the status.
5. System prints the receipt: order number, status, Restaurant, each order line with its line total, the subtotal and the delivery fee, the delivery address and the payment method.
6. System empties the cart.
7. Customer can view their order any time later (UC7).

**Extensions**

- \*a. The Customer abandons the operation or enters an unknown command. System reports it with an error. (BR-9)
- 2a. The cart is empty. System reports that the cart must contain at least one menu item. (BR-4)
- 2b. The Customer has no delivery address. System refuses to continue and asks them to complete the registration. (BR-4, UC1)
- 3a. The price is not positive or the quantity is not in the range. System stops placing the order and reports it. (BR-1)
- 4a. The order number is not greater than every number already given out, so it cannot be assigned. System reports it to the Customer. (BR-6)
- 5a. The receipt cannot be shown although the order number and the status are fine. System reports that the receipt is unavailable and that the Customer can see the order through UC7.

**Special requirements**

- Order numbers are unique and increasing within one run of the program. (BR-6)
- A rule violation never terminates the program. (BR-9)

**Technology and data variations list**

- Cash is the only payment method in this iteration.
- Orders are held in memory only.

**Frequency of occurrence**

Once at the end of each ordering session.

**Open issues**

- Should the System ask for an explicit confirmation between step 3 and step 4?
