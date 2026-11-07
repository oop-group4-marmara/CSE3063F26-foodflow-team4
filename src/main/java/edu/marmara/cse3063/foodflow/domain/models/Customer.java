package edu.marmara.cse3063.foodflow.domain.models;

import java.util.Objects;

public class Customer
{
    private String name;
    private final String email;
    private Address address;
    private final Cart cart = new Cart();

    public Customer(String name, String email, Address address)
    {
        setName(name);
        this.email = Objects.requireNonNull(email, "email");
        setAddress(address);
        if (!email.contains("@"))
        {
            throw new IllegalArgumentException("Email must contain @.");
        }
    }

    public String getName()
    {
        return name;
    }

    public final void setName(String name)
    {
        this.name = Objects.requireNonNull(name, "name");
    }

    public String getEmail()
    {
        return email;
    }

    public Address getAddress()
    {
        return address;
    }

    public final void setAddress(Address address)
    {
        this.address = Objects.requireNonNull(address, "address");
    }

    public Cart getCart()
    {
        return cart;
    }
}
