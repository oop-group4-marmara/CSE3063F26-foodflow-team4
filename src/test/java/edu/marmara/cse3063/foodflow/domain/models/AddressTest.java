package edu.marmara.cse3063.foodflow.domain.models;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AddressTest
{
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void rejectsMissingDistrict(String district)
    {
        assertThrows(IllegalArgumentException.class, () -> new Address(district, "Moda Street"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t\n"})
    void rejectsMissingStreet(String street)
    {
        assertThrows(IllegalArgumentException.class, () -> new Address("Kadıköy", street));
    }
}
