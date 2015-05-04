package io.beanmapper.testmodel.encapsulate;


import io.beanmapper.annotations.BeanProperty;

public class ResultManyToOne {
    private String name;
    @BeanProperty(name = "address.street")
    private String street;
    @BeanProperty(name = "address.number")
    private int number;
    @BeanProperty(name = "address.country.name")
    private String countryName;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }
}
