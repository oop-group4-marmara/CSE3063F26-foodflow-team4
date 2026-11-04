package edu.marmara.cse3063.foodflow.domain;

import java.util.Optional;

public interface CustomerRepository
{
    Customer saveCustomer(Customer customer);
    Optional<Customer> findCustomerByEmail(String email);
    boolean customerExistsByEmail(String email);
}
