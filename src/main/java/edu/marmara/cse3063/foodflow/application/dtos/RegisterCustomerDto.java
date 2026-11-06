package edu.marmara.cse3063.foodflow.application.dtos;

/** Customer registration values supplied by the UI; validation belongs to the service and domain. */
public final class RegisterCustomerDto implements IDto
{
    private final String name;
    private final String email;
    private final String district;
    private final String street;

    public RegisterCustomerDto(String name, String email, String district, String street)
    {
        this.name = name;
        this.email = email;
        this.district = district;
        this.street = street;
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
