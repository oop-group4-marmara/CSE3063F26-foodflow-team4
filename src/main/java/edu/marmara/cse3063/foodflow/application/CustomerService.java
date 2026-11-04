package edu.marmara.cse3063.foodflow.application;

import edu.marmara.cse3063.foodflow.domain.*;
import java.util.Optional;

public class CustomerService
{
    private final CustomerRepository repository;
    private Customer activeCustomer;

    public CustomerService(CustomerRepository repo)
    {
        this.repository = repo;
    }

    public Customer registerCustomer(String name ,String email , String district , String street)
    {
        if(repository.customerExistsByEmail(email))
        {
            throw new IllegalStateException("Email '"+email +"' is already registered. Please log in as existing customer.");
        }
        Customer newCustomer = new Customer(name,email,district,street);
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




}
