# Supplementary specification (FURPS+)

Write requirements that can be *checked*. "Fast" is not checkable; "the restaurant list appears within 1 second for 100 restaurants" is.

| Category | Requirement | How we check it |
|---|---|---|
| Functionality (cross-cutting) | e.g. all monetary amounts are shown with 2 decimals and "TL" | |
| Usability | | |
| Reliability | e.g. invalid input never terminates the program | |
| Performance | | |
| Supportability | | |
| + Implementation / constraints | e.g. Java 21+, Maven, no database in I1 | |
| + Interfaces | e.g. data is read from data/restaurants.json | |
| + Legal / business rules | see BR-1 … BR-9 | |
