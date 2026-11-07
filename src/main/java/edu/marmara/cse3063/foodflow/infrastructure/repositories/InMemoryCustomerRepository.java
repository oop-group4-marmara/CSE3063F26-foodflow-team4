package edu.marmara.cse3063.foodflow.infrastructure.repositories;
import edu.marmara.cse3063.foodflow.domain.models.Customer;
import edu.marmara.cse3063.foodflow.application.repositories.ICustomerRepository;

import java.util.Optional;
import java.util.Map;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;



public class InMemoryCustomerRepository implements ICustomerRepository
{
    //store the users in a HashMap.
    private final Map<String, Customer> store = new ConcurrentHashMap<>();

    @Override
    public Customer saveCustomer(Customer customer)
    {
        store.put(customer.getEmail().trim().toLowerCase(Locale.ROOT),customer);
        return customer;
    }
    @Override
    public Optional<Customer> findCustomerByEmail(String email)
    {
        if(email==null) return Optional.empty();
        return Optional.ofNullable(store.get(email.trim().toLowerCase(Locale.ROOT)));
    }
    @Override
    public boolean customerExistsByEmail(String email)
    {
        if(email==null) return false;
        return store.containsKey(email.trim().toLowerCase(Locale.ROOT));
    }
}
