package edu.marmara.cse3063.foodflow.domain.models;

import java.util.Objects;

public record Address(String district, String street) //I use record intead of a class because we can easily compare values with records and syntax is much easier.
{
    public Address
    {
        Objects.requireNonNull(district, "district");
        Objects.requireNonNull(street, "street");
    }
}
