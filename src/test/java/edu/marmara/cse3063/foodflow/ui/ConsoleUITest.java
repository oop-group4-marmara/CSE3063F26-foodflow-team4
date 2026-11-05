package edu.marmara.cse3063.foodflow.ui;

import edu.marmara.cse3063.foodflow.application.dtos.CustomerDto;
import edu.marmara.cse3063.foodflow.application.dtos.RegisterCustomerDto;
import edu.marmara.cse3063.foodflow.application.dtos.RestaurantDto;
import edu.marmara.cse3063.foodflow.application.interfaces.ICustomerService;
import edu.marmara.cse3063.foodflow.application.interfaces.IRestaurantService;
import edu.marmara.cse3063.foodflow.application.services.CustomerService;
import edu.marmara.cse3063.foodflow.infrastructure.repositories.InMemoryCustomerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Timeout(5)
class ConsoleUITest
{
    @Test
    void welcomeMenuRetriesUnknownOptionsThenExits()
    {
        RecordingCustomerService service = new RecordingCustomerService();
        String output = run("9\n0\n", service);

        assertTrue(output.contains("1. Register a new customer"));
        assertTrue(output.contains("2. Continue with an existing e-mail"));
        assertTrue(output.contains("Please enter one of the available options."));
        assertEquals(1, occurrences(output, "Goodbye."));
        assertEquals(0, service.registrationCalls);
        assertEquals(0, service.loginCalls);
    }

    @Test
    void completedRegistrationPassesFieldsToServiceAndOpensCustomerMenu()
    {
        RecordingCustomerService service = new RecordingCustomerService();
        String output = run("1\nBob Smith\nbob@example.com\nKadıköy\nModa Street\n9\n0\n", service);

        assertEquals(1, service.registrationCalls);
        assertEquals("Bob Smith", service.registration.getName());
        assertEquals("bob@example.com", service.registration.getEmail());
        assertEquals("Kadıköy", service.registration.getDistrict());
        assertEquals("Moda Street", service.registration.getStreet());
        assertTrue(output.contains("Customer: Bob Smith"));
        assertTrue(output.contains("E-mail: bob@example.com"));
        assertTrue(output.contains("2. Change customer"));
        assertTrue(output.contains("Please enter one of the available options."));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "1\n0\n0\n",
            "1\nBob Smith\n 0 \n0\n",
            "1\nBob Smith\nbob@example.com\n0\n0\n",
            "1\nBob Smith\nbob@example.com\nKadıköy\n0\n0\n"
    })
    void registrationCanBeCancelledAtEveryFieldWithoutSubmitting(String script)
    {
        RecordingCustomerService service = new RecordingCustomerService();
        String output = run(script, service);

        assertEquals(0, service.registrationCalls);
        assertTrue(service.getActiveCustomer().isEmpty());
        assertEquals(2, occurrences(output, "1. Register a new customer"));
        assertEquals(1, occurrences(output, "Goodbye."));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "", "1\n", "1\nBob Smith\n", "1\nBob Smith\nbob@example.com\n",
            "1\nBob Smith\nbob@example.com\nKadıköy\n", "2\n"
    })
    void endOfInputBeforeSubmittingAnyFormExitsWithoutPartialChanges(String script)
    {
        RecordingCustomerService service = new RecordingCustomerService();
        String output = run(script, service);

        assertEquals(0, service.registrationCalls);
        assertEquals(0, service.loginCalls);
        assertEquals(1, occurrences(output, "Goodbye."));
    }

    @Test
    void endOfInputAtCustomerMenuExitsNormally()
    {
        RecordingCustomerService service = new RecordingCustomerService();
        String output = run("1\nBob Smith\nbob@example.com\nKadıköy\nModa Street\n", service);

        assertEquals(1, service.registrationCalls);
        assertTrue(output.contains("Customer: Bob Smith"));
        assertEquals(1, occurrences(output, "Goodbye."));
    }

    @Test
    void loginCanBeCancelledWithoutCallingService()
    {
        RecordingCustomerService service = new RecordingCustomerService();
        String output = run("2\n 0 \n0\n", service);

        assertEquals(0, service.loginCalls);
        assertEquals(2, occurrences(output, "1. Register a new customer"));
        assertTrue(output.contains("Enter 0 to go back."));
    }

    @ParameterizedTest
    @MethodSource("validationErrors")
    void expectedRegistrationErrorsReturnToWelcomeMenu(RuntimeException error)
    {
        RecordingCustomerService service = new RecordingCustomerService();
        service.registrationFailure = error;
        String output = run("1\nBob Smith\nbob@example.com\nKadıköy\nModa Street\n0\n", service);

        assertTrue(output.contains("Error: " + error.getMessage()));
        assertEquals(2, occurrences(output, "1. Register a new customer"));
        assertTrue(service.getActiveCustomer().isEmpty());
    }

    @Test
    void failedLoginReturnsToWelcomeAndAllowsRetryWithAnotherEmail()
    {
        CustomerService service = new CustomerService(new InMemoryCustomerRepository());
        service.registerCustomer(new RegisterCustomerDto("Bob Smith", "bob@example.com", "Kadıköy", "Moda Street"));
        service.logout();

        String output = run("2\ninvalid\n2\nmissing@example.com\n2\nbob@example.com\n0\n", service);

        assertEquals(2, occurrences(output, "Error: "));
        assertTrue(output.contains("Customer: Bob Smith"));
        assertEquals("bob@example.com", service.getActiveCustomer().orElseThrow().getEmail());
    }

    @Test
    void invalidRegistrationEmailReturnsToWelcomeBeforeAskingForAddress()
    {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        CustomerService service = new CustomerService(repository);

        String output = run("1\nCihangir\ncihangir\n0\n", service);

        assertTrue(output.contains("Error: E-mail is not in the correct format: cihangir"));
        assertFalse(output.contains("District: "));
        assertFalse(output.contains("Street: "));
        assertEquals(2, occurrences(output, "1. Register a new customer"));
        assertFalse(repository.customerExistsByEmail("cihangir"));
        assertTrue(service.getActiveCustomer().isEmpty());
        assertEquals(1, occurrences(output, "Goodbye."));
    }

    @Test
    void earlyEmailErrorAllowsStartingAValidRegistration()
    {
        CustomerService service = new CustomerService(new InMemoryCustomerRepository());

        String output = run("1\nCihangir\ncihangir\n"
                + "1\nCihangir\ncihangir@example.com\nKadıköy\nModa Street\n0\n", service);

        assertEquals(1, occurrences(output, "Error: "));
        assertEquals(1, occurrences(output, "District: "));
        assertEquals(1, occurrences(output, "Street: "));
        assertTrue(output.indexOf("Error: ") < output.indexOf("District: "));
        assertTrue(output.contains("Customer: Cihangir"));
        assertEquals("cihangir@example.com", service.getActiveCustomer().orElseThrow().getEmail());
    }

    @Test
    void registerChangeCustomerAndLoginKeepRegisteredCustomersInMemory()
    {
        InMemoryCustomerRepository repository = new InMemoryCustomerRepository();
        CustomerService service = new CustomerService(repository);

        String output = run("1\nBob Smith\nbob@example.com\nKadıköy\nModa Street\n2\n2\nBOB@EXAMPLE.COM\n0\n", service);

        assertEquals(2, occurrences(output, "Customer: Bob Smith"));
        assertEquals(2, occurrences(output, "1. Register a new customer"));
        assertTrue(repository.customerExistsByEmail("bob@example.com"));
        assertEquals("bob@example.com", service.getActiveCustomer().orElseThrow().getEmail());
        assertFalse(output.contains("Error: "));
    }

    @Test
    void duplicateRegistrationDoesNotPreventLoggingIntoTheOriginalCustomer()
    {
        CustomerService service = new CustomerService(new InMemoryCustomerRepository());
        String output = run("1\nBob Smith\nbob@example.com\nKadıköy\nModa Street\n2\n"
                + "1\nOther Customer\nBOB@EXAMPLE.COM\n"
                + "2\nbob@example.com\n0\n", service);

        assertTrue(output.contains("Error: "));
        assertEquals(1, occurrences(output, "District: "));
        assertEquals(1, occurrences(output, "Street: "));
        assertFalse(output.contains("Please enter one of the available options."));
        assertEquals("Bob Smith", service.getActiveCustomer().orElseThrow().getName());
        assertFalse(output.contains("Customer: Other Customer"));
    }

    @Test
    void unexpectedServiceFailuresAreNotConvertedToValidationMessages()
    {
        RecordingCustomerService service = new RecordingCustomerService();
        UnsupportedOperationException failure = new UnsupportedOperationException("Unexpected failure");
        service.registrationFailure = failure;

        assertSame(failure, assertThrows(UnsupportedOperationException.class,
                () -> run("1\nBob Smith\nbob@example.com\nKadıköy\nModa Street\n", service)));
    }

    private static Stream<RuntimeException> validationErrors()
    {
        return Stream.of(new IllegalArgumentException("Invalid registration."),
                new IllegalStateException("E-mail is already registered."));
    }

    private String run(String script, ICustomerService service)
    {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (Scanner scanner = new Scanner(script);
             PrintStream output = new PrintStream(bytes, true, StandardCharsets.UTF_8))
        {
            new ConsoleUI(service, new EmptyRestaurantService(), new ConsoleInput(scanner, output), output).run();
        }
        return bytes.toString(StandardCharsets.UTF_8);
    }

    private static int occurrences(String text, String part)
    {
        return text.split(java.util.regex.Pattern.quote(part), -1).length - 1;
    }

    private static final class RecordingCustomerService implements ICustomerService
    {
        private CustomerDto active;
        private RegisterCustomerDto registration;
        private RuntimeException registrationFailure;
        private int registrationCalls;
        private int loginCalls;

        @Override
        public void validateRegistrationEmail(String email)
        {
            // Validation behavior is exercised with the real service in dedicated flow tests.
        }

        @Override
        public CustomerDto registerCustomer(RegisterCustomerDto request)
        {
            registrationCalls++;
            registration = request;
            if (registrationFailure != null)
            {
                throw registrationFailure;
            }
            active = new CustomerDto(request.getName(), request.getEmail(), request.getDistrict(), request.getStreet());
            return active;
        }

        @Override
        public CustomerDto loginExisting(String email)
        {
            loginCalls++;
            throw new IllegalArgumentException("Unknown e-mail.");
        }

        @Override
        public Optional<CustomerDto> getActiveCustomer()
        {
            return Optional.ofNullable(active);
        }

        @Override
        public void logout()
        {
            active = null;
        }
    }

    private static final class EmptyRestaurantService implements IRestaurantService
    {
        @Override
        public ArrayList<RestaurantDto> getAllRestaurants()
        {
            return new ArrayList<>();
        }

        @Override
        public ArrayList<RestaurantDto> filterRestaurants(String cuisine, String district)
        {
            return new ArrayList<>();
        }

        @Override
        public RestaurantDto getRestaurantById(String id)
        {
            return null;
        }
    }
}
