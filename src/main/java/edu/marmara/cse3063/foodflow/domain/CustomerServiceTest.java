package edu.marmara.cse3063.foodflow.domain;



import edu.marmara.cse3063.foodflow.application.CustomerService;
import edu.marmara.cse3063.foodflow.infrastructure.InMemoryCustomerRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;


public class CustomerServiceTest
{
    private CustomerService service ;

    @BeforeEach
    public void  setUp()
    {
        this.service = new CustomerService( new InMemoryCustomerRepository());
    }

    @Test
    @DisplayName("1. Valid Registration: Creates customer and sets active session")
    void testValidRegistration() {
        Customer customer = service.registerCustomer("Alice Smith", "alice@example.com", "Kadikoy", "Moda St.");

        assertNotNull(customer);
        assertEquals("alice@example.com", customer.getEmail());
        assertTrue(service.getActiveCustomer().isPresent());
        assertEquals(customer, service.getActiveCustomer().get());
    }

    @ParameterizedTest
    @CsvSource({
            "'', email@test.com, District, Street",
            "Name, '', District, Street",
            "Name, email@test.com, '', Street",
            "Name, email@test.com, District, ''",
            "'   ', email@test.com, District, Street"
    })
    @DisplayName("2. Empty Field Rejection: Fails if any parameter is null, empty, or blank")
    void testEmptyFieldsRejection(String name, String email, String district, String street) {
        assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer(name, email, district, street));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "plainaddress", "@missinguser.com", "user@.com", "user@domain"})
    @DisplayName("3. Invalid E-mail Format: Fails when email format is invalid")
    void testInvalidEmailFormatRejection(String invalidEmail) {
        assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer("John Doe", invalidEmail, "District", "Street"));
    }
    @Test
    @DisplayName("4. Duplicate E-mail Prevention: Rejects registration with existing email (case-insensitive)")
    void testDuplicateEmailRejection() {
        service.registerCustomer("First User", "user@example.com", "District1", "Street1");

        // Attempting to register again with matching or mixed-case email must fail
        assertThrows(IllegalStateException.class,
                () -> service.registerCustomer("Second User", "USER@EXAMPLE.COM", "District2", "Street2"));
    }
    @Test
    @DisplayName("5. Existing Customer Login: Sets active customer using email case-insensitively")
    void testExistingCustomerLogin() {
        service.registerCustomer("Bob", "bob@example.com", "Beşiktaş", "Çarşı St.");

        // Clear active session to simulate starting fresh
        CustomerService freshSessionService = new CustomerService(new InMemoryCustomerRepository());

        // Re-populate repository to emulate pre-existing data
        CustomerRepository repo = new InMemoryCustomerRepository();
        repo.saveCustomer(new Customer("Bob", "bob@example.com", "Beşiktaş", "Çarşı St."));
        CustomerService loginService = new CustomerService(repo);

        Customer loggedIn = loginService.loginExisting("BOB@EXAMPLE.COM");
        assertEquals("Bob", loggedIn.getName());
        assertTrue(loginService.getActiveCustomer().isPresent());
        assertEquals(loggedIn, loginService.getActiveCustomer().get());
    }
    @Test
    @DisplayName("6. Unknown E-mail Login: Rejects unknown email and leaves session empty")
    void testUnknownEmailLogin() {
        assertThrows(IllegalArgumentException.class,
                () -> service.loginExisting("nonexistent@example.com"));

        assertTrue(service.getActiveCustomer().isEmpty());
    }

}
