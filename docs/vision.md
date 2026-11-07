# Vision and scope

### Problem statement
Customers cannot easily compare restaurants or see the full price (items plus delivery fee) before ordering, restaurants lose orders through miscommunication, and couriers have no reliable way to find delivery jobs. FoodFlow connects Customers, Restaurants and Couriers on one platform and keeps every order traceable from placement to delivery.

### Actors
- **Customer:** finds a meal quickly, sees the full price before ordering, and receives the order correctly.
- **Restaurant owner:** publishes the menu and reaches more customers.
- **Courier:** goes online, receives delivery jobs and earns money.
- **Platform administrator:** oversees restaurants, couriers and orders; configures fees and promotions.

### Key features (whole product)
Customers browse, search and filter restaurants, manage a cart, place orders (respecting opening hours and minimum basket), pay by several methods, use promotions, track orders, and rate restaurants and couriers. Restaurant owners manage menus and accept or reject orders. Couriers go online and are assigned deliveries automatically. The delivery fee is distance-based. Administrators oversee the platform.

### Iteration 1 scope
A Customer can:
- **Identify:** register with name, e-mail and delivery address (district and street), or continue by e-mail.
- **Browse and filter:** list restaurants with cuisine, district and delivery fee; filter by cuisine and/or district.
- **View a menu:** items grouped by category, with prices.
- **Manage a cart:** add, change and remove items; view line totals, subtotal, delivery fee and total.
- **Place an order:** cash on delivery; the order gets a number and status PLACED, a receipt is printed, the cart is emptied.
- **My orders:** list the orders placed in this session.

**Out of scope:** owner, courier and administrator functions; status changes after PLACED, opening hours, minimum basket; promotions, distance-based fees, non-cash payment; tracking, notifications, ratings; authentication beyond e-mail, persistence between runs, graphical interface.

### Assumptions and constraints
**Assumptions:**
- A Customer is identified only by a unique e-mail address (BR-8); there are no passwords.
- A Customer has one delivery address; all orders are cash on delivery.
- Restaurants and menus are loaded at start-up from `data/restaurants.json`.
- Amounts are in TL, with a flat delivery fee per restaurant.
- One Customer uses the application at a time.

**Constraints:**
- Java 21, Maven 3.9+, Gson and JUnit; console interface only.
- Layered architecture with packages `domain`, `application`, `infrastructure` and `ui`.
- Changes are merged into `main` through reviewed pull requests; the submission is the annotated tag `iteration-1`.