package io.beanmapper.testmodel.encapsulate;

public class Address {

    private String street;
    private int number;
    private Country country;

    public Address() {
    }

    public Address(String street, int number, String countryName) {
        this.street = street;
        this.number = number;
        this.country = new Country(countryName);
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }
}
