package edu.marmara.cse3063.foodflow.application.dtos;

import java.util.Objects;

/** An immutable customer snapshot without domain objects or a live cart. */
public final class CustomerDto implements IDto
{
    private final String name;
    private final String email;
    private final String district;
    private final String street;

    public CustomerDto(String name, String email, String district, String street)
    {
        this.name = Objects.requireNonNull(name, "name");
        this.email = Objects.requireNonNull(email, "email");
        this.district = Objects.requireNonNull(district, "district");
        this.street = Objects.requireNonNull(street, "street");
    }

    public String getName()
    {
        return name;
    }

    public String getEmail()
    {
        return email;
    }

    public String getDistrict()
    {
        return district;
    }

    public String getStreet()
    {
        return street;
    }
}
