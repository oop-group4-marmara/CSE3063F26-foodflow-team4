package edu.marmara.cse3063.foodflow.application.interfaces;

import edu.marmara.cse3063.foodflow.application.dtos.CustomerDto;
import edu.marmara.cse3063.foodflow.application.dtos.RegisterCustomerDto;

import java.util.Optional;

public interface ICustomerService {
    /** Checks registration e-mail rules without saving a customer or opening a session. */
    void validateRegistrationEmail(String email);
    CustomerDto registerCustomer(RegisterCustomerDto request);
    CustomerDto loginExisting(String email);
    Optional<CustomerDto> getActiveCustomer();
    void logout();
}
