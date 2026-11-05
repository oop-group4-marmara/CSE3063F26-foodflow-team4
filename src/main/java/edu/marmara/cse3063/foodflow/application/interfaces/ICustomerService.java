package edu.marmara.cse3063.foodflow.application.interfaces;

import edu.marmara.cse3063.foodflow.domain.models.Address;
import edu.marmara.cse3063.foodflow.domain.models.Customer;

import java.util.Optional;

public interface ICustomerService {
    public Customer registerCustomer(String email, String name, Address address);
    public Customer loginExisting(String email);
    public Optional<Customer> getActiveCustomer();
}
