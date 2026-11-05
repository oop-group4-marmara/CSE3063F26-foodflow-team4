package edu.marmara.cse3063.foodflow.application.services;

import edu.marmara.cse3063.foodflow.application.interfaces.ICustomerService;
import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.infrastructure.interfaces.ICustomerRepository;

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
        this.repository = repo;
    }

    public Customer registerCustomer(String name, String email, Address address) throws Exception {
        String validName = validateName(name);

        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("Email is not in right format " + email);
        }

        String normalizedEmail = email.trim().toLowerCase();

        if (repository.customerExistsByEmail(normalizedEmail))
        {
            throw new IllegalStateException("Email: "+ normalizedEmail +"' is already registered. Please log in as existing customer.");
        }
        Customer newCustomer = new Customer(validName, normalizedEmail, address);
        Customer saved = repository.saveCustomer(newCustomer);
        this.activeCustomer= saved;
        return saved;
    }

    public Customer loginExisting(String email)
    {
        if(email==null || email.trim().isEmpty())
        {
            throw new IllegalArgumentException("E-mail Cannot Be Empty.");
        }

        Optional<Customer> optCustomer = repository.findCustomerByEmail(email);
        if(optCustomer.isEmpty())
        {
            throw new IllegalArgumentException("Customer With E-mail '" + email +"' not found.");
        }

        Customer customer = optCustomer.get();
        activeCustomer=customer;
        return customer;
    }

    public Optional<Customer> getActiveCustomer()
    {
        return Optional.ofNullable(activeCustomer);
    }

    private boolean isValidEmail(String email) {
        return email != null
                && !email.trim().isEmpty()
                && EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}
