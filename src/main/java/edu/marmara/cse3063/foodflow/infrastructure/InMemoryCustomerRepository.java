package edu.marmara.cse3063.foodflow.infrastructure;
import edu.marmara.cse3063.foodflow.domain.Customer;
import edu.marmara.cse3063.foodflow.domain.CustomerRepository;

import java.util.Optional;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



public class InMemoryCustomerRepository implements CustomerRepository
{

    private final Map<String,Customer> store = new ConcurrentHashMap<>();

    @Override
    public Customer saveCustomer(Customer customer)
    {
        store.put(customer.getEmail(),customer);
        return customer;
    }
    @Override
    public Optional<Customer> findCustomerByEmail(String email)
    {
        if(email==null) return Optional.empty();
        return Optional.ofNullable(store.get(email.trim().toLowerCase()));
    }
    @Override
    public boolean customerExistsByEmail(String email)
    {
        if(email==null) return false;
        return store.containsKey(email.trim().toLowerCase());
    }
}
