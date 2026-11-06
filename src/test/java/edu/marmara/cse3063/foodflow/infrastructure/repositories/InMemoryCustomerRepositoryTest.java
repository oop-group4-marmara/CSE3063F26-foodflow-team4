package edu.marmara.cse3063.foodflow.infrastructure.repositories;

import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryCustomerRepositoryTest
{
    @Test
    @ResourceLock("java.util.Locale")
    void emailLookupIsIndependentOfTurkishLocale() throws Exception
    {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
            Customer customer = new Customer("Alice", " ALICE@EXAMPLE.COM ", new Address("District", "Street"));
            repository.saveCustomer(customer);

            assertSame(customer, repository.findCustomerByEmail("alice@example.com").orElseThrow());
            assertTrue(repository.customerExistsByEmail(" alice@example.com "));
            assertTrue(repository.customerExistsByEmail("ALICE@EXAMPLE.COM"));
        } finally {
            Locale.setDefault(original);
        }
    }
}
