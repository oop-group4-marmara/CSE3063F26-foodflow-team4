package edu.marmara.cse3063.foodflow.application.repositories;

import edu.marmara.cse3063.foodflow.domain.models.Customer;

import java.util.Optional;

public interface ICustomerRepository
{
    Customer saveCustomer(Customer customer);
    Optional<Customer> findCustomerByEmail(String email);
    boolean customerExistsByEmail(String email);
}
