# Supplementary Specification (FURPS+), Iteration 1

Each requirement is checkable: it can be verified by a yes/no inspection, a unit test, or a measurement.
Terms follow `glossary.md`. BR-x refers to the business rules in the Iteration 1 brief.

**Verification methods:** T = automated JUnit test, D = demo / manual console run, I = inspection (code or document review), M = measurement.

## F: Functionality

| ID   | Requirement                                                                                                                                              | BR   | Check |
|------|----------------------------------------------------------------------------------------------------------------------------------------------------------|------|-------|
| F-1  | A Customer can register with name, e-mail address and Address (district + street), or continue as an existing Customer by e-mail address.               | BR-8 | D     |
| F-2  | Registration is refused when the e-mail address does not contain `@` or is already used by another Customer.                                             | BR-8 | T     |
| F-3  | The restaurant list shows all 6 restaurants from `restaurants.json`, each with cuisine, district and delivery fee.                                       |      | D     |
| F-4  | Filtering by cuisine and/or district returns only matching restaurants; an empty result prints a polite "no restaurants found" message, not an error.   |      | T, D  |
| F-5  | A menu is shown grouped by Category, with a price for each menu item.                                                                                    |      | D     |
| F-6  | Adding a menu item with a quantity from 1 to 20 succeeds; quantities 0 and 21 are rejected.                                                              | BR-1 | T     |
| F-7  | Adding an item already in the cart increases the quantity of the existing Cart line; the cart never has two lines for the same menu item.               | BR-3 | T     |
| F-8  | Adding an item from a second restaurant is refused and the cart stays unchanged.                                                                         | BR-2 | T     |
| F-9  | The cart view shows every line total, the subtotal, the delivery fee and the total; Total = Subtotal + Delivery fee.                                     | BR-5 | T, D  |
| F-10 | A Customer can change a quantity and remove an item; changed quantities obey the 1 to 20 range.                                                          | BR-1 | T     |
| F-11 | Placing an order fails when the cart is empty or the Customer has no Address.                                                                            | BR-4 | T     |
| F-12 | A successful order gets a unique order number, larger than the previous one, and the status PLACED.                                                      | BR-6 | T     |
| F-13 | After placing an order the cart is empty, and a receipt shows order number, order lines, subtotal, delivery fee, total and "cash on delivery".          | BR-7 | T, D  |
| F-14 | An Order Line stores the item name and unit price at placement time; a later change to a menu item does not change an existing order's total.          | BR-7 | T     |
| F-15 | (Could) "My orders" lists the orders the current Customer placed in this session, and only those.                                                        |      | D     |

## U: Usability

| ID  | Requirement                                                                                                                                                           | BR   | Check |
|-----|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------|------|-------|
| U-1 | Every monetary amount shown to the user has exactly two decimals followed by " TL" (e.g. `305.00 TL`). Checked on restaurant list, menu, cart and receipt.            | BR-5 | T, D  |
| U-2 | Every Invalid input produces one clear message that says what was wrong and what the user can do (e.g. "Your cart has items from Kebapçı Mehmet. Empty it first.").   | BR-9 | D     |
| U-3 | A `help` command lists all commands with their syntax.                                                                                                                | BR-9 | D     |
| U-4 | Turkish characters (ç, ğ, ı, ö, ş, ü, İ) in restaurant and item names are displayed correctly on the console (UTF-8).                                                  |      | D     |
| U-5 | A first-time user can complete the demo path (register, filter, add items, place order) using only `help` and on-screen messages, with no more than 10 commands.      |      | D     |
| U-6 | The console UI uses the glossary terms (Cart, Order, Menu item, Delivery fee, Subtotal, Total) in all output.                                                         |      | I     |

## R: Reliability

| ID  | Requirement                                                                                                                                                  | BR   | Check |
|-----|--------------------------------------------------------------------------------------------------------------------------------------------------------------|------|-------|
| R-1 | Invalid input (unknown command, missing argument, non-numeric quantity, unknown item or restaurant id, blank or malformed e-mail) never terminates the program; the prompt returns. | BR-9 | D     |
| R-2 | No uncaught exception or stack trace is shown to the user during the demo script or when entering invalid values.                                            | BR-9 | D     |
| R-3 | A refused operation (wrong quantity, other restaurant, empty cart) leaves the cart and order list unchanged.                                                 | BR-2 | T     |
| R-4 | Domain objects are validated in their constructors: a MenuItem with price ≤ 0, a Cart line with quantity outside 1 to 20 or a Customer with an invalid e-mail cannot be created. | BR-1, BR-8 | T     |
| R-5 | Order numbers are unique and strictly increasing across all orders in a run.                                                                                 | BR-6 | T     |
| R-6 | If `restaurants.json` is missing or malformed, the program prints a clear error message and exits cleanly (no stack trace) instead of starting with partial data. | BR-9 | D     |

## P: Performance

| ID  | Requirement                                                                                          | Check |
|-----|------------------------------------------------------------------------------------------------------|-------|
| P-1 | Loading `restaurants.json` (6 restaurants, 35 items) at start-up takes at most 1 second.             | M     |
| P-2 | Every console command (list, filter, add, view cart, place order) responds within 200 ms.            | M     |
| P-3 | `mvn -q test` finishes in under 60 seconds on a normal student laptop.                               | M     |

## S: Supportability

| ID  | Requirement                                                                                                                                         | Check |
|-----|-----------------------------------------------------------------------------------------------------------------------------------------------------|-------|
| S-1 | The code is split into the packages `domain`, `application`, `infrastructure` and `ui` under `edu.marmara.cse3063.foodflow`.                         | I     |
| S-2 | No class in `domain` calls `System.out` or reads `System.in`; the `ui` package contains no business rules (BR-1 to BR-8).                           | I     |
| S-3 | Only `infrastructure` references Gson or `restaurants.json`.                                                                                        | I     |
| S-4 | Each rule BR-1 to BR-8 is implemented in one class, named in the glossary or report.                                                                | I     |
| S-5 | All fields are private, collections are returned as copies or unmodifiable views, and there is no mutable static state except deliberate counters. | I     |
| S-6 | Class, method and field names follow the glossary and the naming conventions (PascalCase classes, camelCase methods, UPPER_SNAKE_CASE constants).   | I     |
| S-7 | The README lets a new person build and run the application with `mvn -q test` and `mvn -q compile exec:java` only.                                 | D     |
| S-8 | Adding a new requirement from I2 (opening hours, minimum basket) needs no change to the `ui` package's rule handling; the loader already reads `minimumBasket`. | I     |
| S-9 | PlantUML diagrams in `docs/diagrams` match the code and were updated in the same pull request as the related code change.                          | I     |

## +: Additional categories

| Category                   | Relevant?                | ID    | Requirement or reason                                                                                                                         | Check |
|----------------------------|--------------------------|-------|-----------------------------------------------------------------------------------------------------------------------------------------------|-------|
| Design constraints         | Yes                      | DC-1  | The system uses Java 21, Maven 3.9+, Gson and JUnit, with a console UI only.                                                                  | I     |
| Design constraints         | Yes                      | DC-2  | The architecture is a layered n-tier architecture (see S-1).                                                                                  | I     |
| Implementation constraints | Yes                      | IC-1  | Restaurants and menus are loaded from `data/restaurants.json` at start-up; no restaurant data is hard-coded in Java.                          | I     |
| Implementation constraints | Yes                      | IC-2  | All data is in memory only; no file or database is written when the program runs.                                                             | I     |
| Implementation constraints | Yes                      | IC-3  | No mutable static lists of restaurants or orders exist.                                                                                       | I     |
| Interface constraints      | Yes                      | IF-1  | `restaurants.json` is UTF-8 with the structure `restaurants[]` → `id`, `name`, `cuisine`, `district`, `deliveryFee`, `minimumBasket`, `menu[]` (`id`, `name`, `category`, `price`). | T     |
| Business rules / currency  | Yes                      | BRU-1 | All amounts are in Turkish Lira (TL); the only payment method is cash on delivery.                                                            | D     |
| Packaging / delivery       | Yes                      | PK-1  | The submission is the annotated tag `iteration-1` on `main`, plus the report PDF and the zip (under 10 MB), uploaded by one team member only. | I     |
| Localisation / i18n        | Partly relevant          |,     | Only the Turkish characters in data need correct display (U-4). Multiple UI languages are not required.                                       |,     |
| Concurrency / scalability  | Not relevant             |,     | Single user, single session, 6 restaurants and 35 items; no multi-user or large-data needs in I1.                                             |,     |

## Consistency with BR-5 and BR-9

- **BR-5:** F-9, F-13 and U-1 fix the formula (Total = Subtotal + Delivery fee) and the display format (two decimals + " TL").
- **BR-9:** U-2, U-3, R-1, R-2 and R-6 require clear messages, no termination and no stack traces for Invalid input.

## Traceability: business rules → requirements

| BR   | Requirements                      |
|------|-----------------------------------|
| BR-1 | F-6, F-10, R-4                    |
| BR-2 | F-8, R-3                          |
| BR-3 | F-7                               |
| BR-4 | F-11                              |
| BR-5 | F-9, U-1                          |
| BR-6 | F-12, R-5                         |
| BR-7 | F-13, F-14                        |
| BR-8 | F-1, F-2, R-4                     |
| BR-9 | U-2, U-3, R-1, R-2, R-6           |
