package io.beanmapper.testmodel.encapsulate;

public class ResultAddress {

    private String street;
    private int number;
    private ResultCountry country;

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

    public ResultCountry getCountry() {
        return country;
    }

    public void setCountry(ResultCountry country) {
        this.country = country;
    }
}
