package edu.marmara.cse3063.foodflow.domain.models;

import java.util.Objects;

public record Address(String district, String street)
{
    public Address
    {
        Objects.requireNonNull(district, "district");
        Objects.requireNonNull(street, "street");
    }
}
