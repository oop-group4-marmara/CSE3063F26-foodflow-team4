# Vision and scope

## Problem statement
Customers cannot easily compare restaurants or see the full price (items plus delivery fee) before ordering. Restaurants lose orders through miscommunication, and couriers have no reliable way to find delivery jobs. Every failed or mistaken order wastes food, time and money, and nobody oversees the whole flow. FoodFlow connects Customers, Restaurants and Couriers on one platform and keeps every order traceable from placement to delivery.

## Stakeholders and actors
| Stakeholder / actor    | Interest or goal                                                                                                                                                |
|------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------|
| Customer               | Finds a restaurant and a meal quickly, sees the full price (items plus delivery fee) before committing, places an order, and receives it correctly and on time. |
| Restaurant owner       | Publishes their whole menu and makes more sales by reaching more customers                                                                                      |
| Courier                | Goes online, recives delivery jobs and earns money by deliviring food to the customer                                                                           |
| Platform administrator | Keeps marketplace safe and commercially viable; oversees restaurants, couriers and orders. Configures fees and promations                                       |

## Key features (whole product)
1. Customers can browse, search and filter restaurants, view menus, manage a cart and place orders.
2. Customers can manage a cart and place an order; orders respect restaurant opening hours and minimum basket.
3. Customers can pay by several methods, use promotions, track orders and receive notifications.
4. Customers can rate and review restaurants and couriers.
5. Restaurant owners can manage their menus and accept or reject incoming orders.
6. Couriers can go online and are automatically assigned to deliveries.
7. The delivery fee is calculated by distance, and the total shows items plus fee.
8. Platform administrators can oversee restaurants, couriers and orders, and configure fees and promotions.

## Iteration 1 scope
- Identify the customer: Register with name, e-mail and delivery address (district street), or continue as an existing customer by e-mail. In-memory only.
- Browse restaurants: List all restaurants with cuisine, district and delivery fee.
- Filter restaurants: Filter by cuisine and/or district; an empty result is reported politely.
- View a menu: Show a restaurant’s items grouped by category, with prices.
- Manage a cart: Add an item with a quantity, change a quantity, remove an item, view the cart with line totals, subtotal, delivery fee and total.
- Place an order: Cash on delivery to the customer’s address; the order gets a number and the status PLACED; a receipt is printed; the cart is emptied.
- My orders: List the orders the current customer placed in this session.

### Out of scope
- Restaurant owner, courier and administrator functions
- Order status changes after PLACED, opening hours and minimum basket
- Promotions, distance-based fees and payment methods other than cash
- Order tracking, notifications, ratings and reviews
- Authentication beyond e-mail, saving data between runs, graphical interface

## Assumptions and constraints

### Assumptions
- A Customer is identified only by a unique e-mail address (BR-8); there are no passwords or other authentication.
- A Customer has one delivery address (district and street), entered at registration.
- All orders are paid in cash on delivery; no payment processing is simulated in I1.
- Restaurants and menus are loaded once at start-up from `data/restaurants.json` and do not change while the program runs.
- All amounts are in Turkish Lira (TL), and the delivery fee is a flat amount per restaurant (distance-based fees arrive in I2).
- The application is used by one Customer at a time in a single session.

### Constraints
- Technology: Java 21, Maven 3.9+, Gson and JUnit; console user interface only.
- Architecture: Layered n-tier architecture with the packages of `domain`, `application`, `infrastructure` and `ui`.
- Schedule and process: The iteration deadline is fixed; changes reach `main` through reviewed pull requests, and the submission is the annotated tag `iteration-1`.