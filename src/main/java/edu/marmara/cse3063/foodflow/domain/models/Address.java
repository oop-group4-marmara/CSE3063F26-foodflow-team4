package edu.marmara.cse3063.foodflow.domain.models;

public record Address(String district, String street)
{
    public Address
    {
        if (district == null || district.isBlank())
        {
            throw new IllegalArgumentException("District cannot be empty.");
        }
        if (street == null || street.isBlank())
        {
            throw new IllegalArgumentException("Street cannot be empty.");
        }
    }
}
