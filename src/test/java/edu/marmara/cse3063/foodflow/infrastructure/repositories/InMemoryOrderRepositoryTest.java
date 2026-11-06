package edu.marmara.cse3063.foodflow.infrastructure.repositories;

import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.domain.models.Order;
import edu.marmara.cse3063.foodflow.domain.models.OrderLine;
import edu.marmara.cse3063.foodflow.domain.models.Restaurant;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryOrderRepositoryTest
{
    private static Order orderWithNumber(long number) throws Exception
    {
        Customer customer = new Customer("Alice", "alice@example.com", new Address("Kadıköy", "Moda Street"));
        Restaurant restaurant = new Restaurant("R01", "Kebapçı Mehmet", "Turkish", null, "Kadıköy", 30.0);
        return new Order(number, customer, restaurant, customer.getAddress(),
                new ArrayList<>(List.of(new OrderLine("Ayran", 25.0, 1))));
    }

    @Test
    void issuesIncreasingNumbersStartingAtOne()
    {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();

        assertEquals(1, repository.nextOrderNumber());
        assertEquals(2, repository.nextOrderNumber());
        assertEquals(3, repository.nextOrderNumber());
    }

    @Test
    void savesAnOrderWithAnIssuedNumber() throws Exception
    {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        Order order = orderWithNumber(repository.nextOrderNumber());

        assertSame(order, repository.saveOrder(order));
    }

    @Test
    void refusesNumbersThatWereNotIssuedOrAreReused() throws Exception
    {
        InMemoryOrderRepository repository = new InMemoryOrderRepository();
        assertThrows(IllegalStateException.class, () -> repository.saveOrder(orderWithNumber(1)));

        long issued = repository.nextOrderNumber();
        repository.saveOrder(orderWithNumber(issued));
        assertThrows(IllegalStateException.class, () -> repository.saveOrder(orderWithNumber(issued)));
        assertThrows(IllegalStateException.class, () -> repository.saveOrder(orderWithNumber(0)));
    }
}
