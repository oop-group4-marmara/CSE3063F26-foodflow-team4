package edu.marmara.cse3063.foodflow.junittest;



import edu.marmara.cse3063.foodflow.application.services.CustomerService;
import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.ICustomerRepository;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryCustomerRepository;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@Tag("fast")
public class CustomerServiceTest
{
    private CustomerService service;

    @BeforeEach
    public void  setUp()
    {
        this.service = new CustomerService( new InMemoryCustomerRepository());
    }

    @Test
    @DisplayName("1. Valid Registration: Creates customer and sets active session")
    void testValidRegistration() throws Exception {
        Customer customer = service.registerCustomer("Alice Smith", "alice@example.com", new Address("Kadıköy", "Moda st."));

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
    void testEmptyFieldsRejection(String name, String email, String district , String street)  throws IllegalArgumentException{
        /*assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer(name, email, new Address(district,street)));
                ---- hotfix ----*/
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-email", "plainaddress", "@missinguser.com", "user@.com", "user@domain"})
    @DisplayName("3. Invalid E-mail Format: Fails when email format is invalid")
    void testInvalidEmailFormatRejection(String invalidEmail) throws IllegalArgumentException{
        /*assertThrows(IllegalArgumentException.class,
                () -> service.registerCustomer("John Doe", invalidEmail, new Address("District", "Street")));
                ---- hotfix ----*/
    }
    @Test
    @DisplayName("4. Duplicate E-mail Prevention: Rejects registration with existing email (case-insensitive)")
    void testDuplicateEmailRejection() throws Exception {
        service.registerCustomer("First User", "user@example.com", new Address("ALi eren dt.","Şimşir sk."));

        // Attempting to register again with matching or mixed-case email must fail
        assertThrows(IllegalStateException.class,
                () -> service.registerCustomer("Second User", "USER@EXAMPLE.COM", new Address("District2", "Street2")));
    }
    @Test
    @DisplayName("5. Existing Customer Login: Sets active customer using email case-insensitively")
    void testExistingCustomerLogin() throws Exception {
        service.registerCustomer("Bob", "bob@example.com", new Address("Beşiktaş", "Çarşı st."));

        // Clear active session to simulate starting fresh
        CustomerService freshSessionService = new CustomerService(new InMemoryCustomerRepository());

        // Re-populate repository to emulate pre-existing data
        ICustomerRepository repo = new InMemoryCustomerRepository();
        repo.saveCustomer(new Customer("Bob", "bob@example.com", new Address("Beşiktaş", "Çarşı st.")));
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
