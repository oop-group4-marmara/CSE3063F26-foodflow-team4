package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.dtos.CustomerDto;
import edu.marmara.cse3063.foodflow.application.dtos.RegisterCustomerDto;
import edu.marmara.cse3063.foodflow.application.interfaces.ICustomerService;
import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.ICustomerRepository;

import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class CustomerService implements ICustomerService
{
    private final ICustomerRepository repository;
    private Customer activeCustomer;

    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\\.[A-Za-z0-9-]+)*\\.[A-Za-z]{2,}$");

    private static final int NAME_MIN_LENGTH = 2;
    private static final int NAME_MAX_LENGTH = 50;

    private static final Pattern NAME_PATTERN =
            Pattern.compile("^\\p{L}[\\p{L} '.-]*$");

    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be empty.");
        }

        String normalized = name.trim().replaceAll("\\s+", " ");

        if (normalized.length() < NAME_MIN_LENGTH || normalized.length() > NAME_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "Name must be between " + NAME_MIN_LENGTH + " and " + NAME_MAX_LENGTH + " characters.");
        }
        if (!NAME_PATTERN.matcher(normalized).matches()) {
            throw new IllegalArgumentException(
                    "Name can only contain letters, spaces, apostrophes, dots and hyphens: " + name);
        }
        return normalized;
    }

    public CustomerService(ICustomerRepository repo)
    {
        this.repository = Objects.requireNonNull(repo, "repository");
    }

    @Override
    public CustomerDto registerCustomer(RegisterCustomerDto request) {
        if (request == null) {
            throw new IllegalArgumentException("Registration details cannot be null.");
        }

        String validName = validateName(request.getName());
        String email = request.getEmail();
        validateRegistrationEmail(email);

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        Address address = new Address(request.getDistrict(), request.getStreet());
        Customer newCustomer = createCustomer(validName, normalizedEmail, address);
        Customer saved = repository.saveCustomer(newCustomer);
        this.activeCustomer = saved;
        return toDto(saved);
    }

    @Override
    public void validateRegistrationEmail(String email)
    {
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("E-mail is not in the correct format: " + email);
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        if (repository.customerExistsByEmail(normalizedEmail)) {
            throw new IllegalStateException("E-mail '" + normalizedEmail
                    + "' is already registered. Please continue as an existing customer.");
        }
    }

    @Override
    public CustomerDto loginExisting(String email)
    {
        if(email==null || email.trim().isEmpty())
        {
            throw new IllegalArgumentException("E-mail cannot be empty.");
        }

        String normalizedEmail = email.trim().toLowerCase(Locale.ROOT);
        Optional<Customer> optCustomer = repository.findCustomerByEmail(normalizedEmail);
        if(optCustomer.isEmpty())
        {
            throw new IllegalArgumentException("Customer with e-mail '" + normalizedEmail + "' not found.");
        }

        Customer customer = optCustomer.get();
        activeCustomer = customer;
        return toDto(customer);
    }

    @Override
    public Optional<CustomerDto> getActiveCustomer()
    {
        return Optional.ofNullable(activeCustomer).map(CustomerService::toDto);
    }

    @Override
    public void logout()
    {
        activeCustomer = null;
    }

    private static Customer createCustomer(String name, String email, Address address)
    {
        return new Customer(name, email, address);
    }

    private static CustomerDto toDto(Customer customer)
    {
        Address address = customer.getAddress();
        return new CustomerDto(customer.getName(), customer.getEmail(), address.district(), address.street());
    }

    private boolean isValidEmail(String email) {
        return email != null
                && !email.trim().isEmpty()
                && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}
