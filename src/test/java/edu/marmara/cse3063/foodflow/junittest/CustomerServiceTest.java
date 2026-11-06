package edu.marmara.cse3063.foodflow.junittest;

import edu.marmara.cse3063.foodflow.application.dtos.CustomerDto;
import edu.marmara.cse3063.foodflow.application.dtos.RegisterCustomerDto;
import edu.marmara.cse3063.foodflow.application.services.CustomerService;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryCustomerRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class CustomerServiceTest
{
    private InMemoryCustomerRepository repository;
    private CustomerService service;

    @BeforeEach
    public void setUp()
    {
        repository = new InMemoryCustomerRepository();
        service = new CustomerService(repository);
    }

    @Test
    @DisplayName("Valid registration stores the customer and opens an active session")
    void testValidRegistration()
    {
        CustomerDto customer = service.registerCustomer(
                new RegisterCustomerDto("  Alice   Smith  ", " ALICE@example.com ", "Kadıköy", "Moda st."));

        assertEquals("Alice Smith", customer.getName());
        assertEquals("alice@example.com", customer.getEmail());
        assertEquals("Kadıköy", customer.getDistrict());
        assertEquals("Moda st.", customer.getStreet());
        assertTrue(repository.customerExistsByEmail("alice@example.com"));
        assertEquals("alice@example.com", service.getActiveCustomer().orElseThrow().getEmail());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "not-an-email", "user@", "@example.com"})
    @DisplayName("Invalid email is rejected without creating a customer or session")
    void testInvalidEmailRejection(String email)
    {
        assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer(new RegisterCustomerDto("John Doe", email, "District", "Street")));

        assertFalse(repository.customerExistsByEmail(email));
        assertTrue(service.getActiveCustomer().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "cihangir", "user@", "@example.com"})
    void registrationEmailValidationRejectsInvalidEmailWithoutSaving(String email)
    {
        assertThrows(IllegalArgumentException.class, () -> service.validateRegistrationEmail(email));

        assertFalse(repository.customerExistsByEmail(email));
        assertTrue(service.getActiveCustomer().isEmpty());
    }

    @Test
    void registrationEmailValidationDoesNotCreateACustomerOrSession()
    {
        service.validateRegistrationEmail(" ALICE@example.com ");

        assertFalse(repository.customerExistsByEmail("alice@example.com"));
        assertTrue(service.getActiveCustomer().isEmpty());
    }

    @Test
    void registrationEmailValidationRejectsDuplicateWithoutChangingOriginalCustomer()
    {
        service.registerCustomer(new RegisterCustomerDto("Alice", "alice@example.com", "District", "Street"));
        service.logout();

        assertThrows(IllegalStateException.class,
                () -> service.validateRegistrationEmail(" ALICE@EXAMPLE.COM "));

        assertTrue(service.getActiveCustomer().isEmpty());
        assertEquals("Alice", service.loginExisting("alice@example.com").getName());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "A", "John123", "AVeryLongNameThatExceedsTheMaximumAllowedLengthOfFiftyCharacters"})
    @DisplayName("Invalid name is rejected without saving the customer")
    void testInvalidNameRejection(String name)
    {
        assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer(new RegisterCustomerDto(name, "user@example.com", "District", "Street")));

        assertFalse(repository.customerExistsByEmail("user@example.com"));
        assertTrue(service.getActiveCustomer().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Invalid district is rejected without saving the customer")
    void testInvalidDistrictRejection(String district)
    {
        assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer(new RegisterCustomerDto("John Doe", "user@example.com", district, "Street")));

        assertFalse(repository.customerExistsByEmail("user@example.com"));
        assertTrue(service.getActiveCustomer().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t"})
    @DisplayName("Invalid street is rejected without saving the customer")
    void testInvalidStreetRejection(String street)
    {
        assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer(new RegisterCustomerDto("John Doe", "user@example.com", "District", street)));

        assertFalse(repository.customerExistsByEmail("user@example.com"));
        assertTrue(service.getActiveCustomer().isEmpty());
    }

    @Test
    void testNullRegistrationRejection()
    {
        assertThrows(IllegalArgumentException.class, () -> service.registerCustomer(null));
        assertTrue(service.getActiveCustomer().isEmpty());
    }

    @Test
    @DisplayName("Duplicate email cannot overwrite the original customer")
    void testDuplicateEmailRejection()
    {
        service.registerCustomer(new RegisterCustomerDto("First User", "user@example.com", "District", "Street"));
        service.logout();

        assertThrows(IllegalStateException.class,
                () -> service.registerCustomer(new RegisterCustomerDto("Second User", " USER@EXAMPLE.COM ", "District2", "Street2")));

        assertTrue(service.getActiveCustomer().isEmpty());
        assertEquals("First User", service.loginExisting("user@example.com").getName());
    }

    @Test
    @DisplayName("Existing customer login accepts case and whitespace differences")
    void testExistingCustomerLogin()
    {
        service.registerCustomer(new RegisterCustomerDto("Bob", "bob@example.com", "Beşiktaş", "Çarşı st."));
        service.logout();

        CustomerDto loggedIn = service.loginExisting(" BOB@EXAMPLE.COM ");

        assertEquals("Bob", loggedIn.getName());
        assertEquals("bob@example.com", service.getActiveCustomer().orElseThrow().getEmail());
    }

    @Test
    @DisplayName("Unknown email leaves the session empty")
    void testUnknownEmailLogin()
    {
        assertThrows(IllegalArgumentException.class,
                () -> service.loginExisting("nonexistent@example.com"));

        assertTrue(service.getActiveCustomer().isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   "})
    void testEmptyLoginEmailRejection(String email)
    {
        assertThrows(IllegalArgumentException.class, () -> service.loginExisting(email));
        assertTrue(service.getActiveCustomer().isEmpty());
    }

    @Test
    @DisplayName("Logging out repeatedly removes the session but preserves the customer")
    void testLogoutIsIdempotentAndPreservesRegistration()
    {
        service.registerCustomer(new RegisterCustomerDto("Alice", "alice@example.com", "District", "Street"));

        service.logout();
        service.logout();

        assertTrue(service.getActiveCustomer().isEmpty());
        assertEquals("Alice", service.loginExisting("alice@example.com").getName());
    }

    @Test
    @ResourceLock("java.util.Locale")
    @DisplayName("Email identity is independent of the Turkish default locale")
    void testTurkishLocaleEmailNormalization()
    {
        Locale original = Locale.getDefault();
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"));
            CustomerDto customer = service.registerCustomer(
                    new RegisterCustomerDto("Alice", "ALICE@EXAMPLE.COM", "District", "Street"));
            assertEquals("alice@example.com", customer.getEmail());

            service.logout();
            assertEquals("Alice", service.loginExisting("alice@example.com").getName());
            assertThrows(IllegalStateException.class,
                    () -> service.registerCustomer(new RegisterCustomerDto("Other User", "ALICE@EXAMPLE.COM", "District", "Street")));
        } finally {
            Locale.setDefault(original);
        }
    }
}
