package edu.marmara.cse3063.foodflow.domain;

import java.util.Objects;
import java.util.regex.Pattern;
public class Customer
{

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private final String name;
    private final String email;
    private final String district;
    private final String street;

    public Customer(String name , String email, String district , String street)
    {
        stringValidateNonEmpty(name,"Name Cannot Be Empty!");
        stringValidateNonEmpty(email , "Email Cannot Be Empty!");
        stringValidateNonEmpty(district , "District Cannot Be Empty!");
        stringValidateNonEmpty(street , "Street Cannot Be Empty!");

        String trimmedEmail = email.trim().toLowerCase();
        if(!(EMAIL_PATTERN.matcher(trimmedEmail).matches()))
        {
            throw new IllegalArgumentException("Wrong Email Pattern : " + email);
        }

        this.name = name;
        this.email = email;
        this.district = district;
        this.street = street;
    }

    private void stringValidateNonEmpty(String str , String errMessage)
    {
        if ( str == null || str.trim().isEmpty())
        {
            throw new IllegalArgumentException(errMessage);
        }
    }

    public String getName() { return this.name;}
    public String getEmail() { return this.email;}
    public String getDistrict() { return this.district;}
    public String getStreet() { return this.street;}

    @Override
    public boolean equals(Object o )
    {
        if( this == o ) return true;
        if(o == null || getClass() != o.getClass()) return false;

        return Objects.equals(this.email, ((Customer) o).email);
    }

    @Override
    public int hashCode()
    {
        return this.email.hashCode();
    }

}
