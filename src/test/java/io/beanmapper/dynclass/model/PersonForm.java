package io.beanmapper.dynclass.model;

public class PersonForm {

    public String name;
    public String street;
    public String houseNumber;
    public String city;

    public PersonForm(String name, String street, String houseNumber, String city) {
        this.name = name;
        this.street = street;
        this.houseNumber = houseNumber;
        this.city = city;
    }
}
