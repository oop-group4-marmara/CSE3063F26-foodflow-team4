package edu.marmara.cse3063.foodflow.application.dtos;

import edu.marmara.cse3063.foodflow.domain.models.Address;

public class AddressDto
{
    private final String district;
    private final String street;

    public AddressDto(String district, String street)
    {
        this.district = district;
        this.street = street;
    }

    public static AddressDto from(Address address)
    {
        return new AddressDto(address.district(), address.street());
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