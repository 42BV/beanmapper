package io.beanmapper.testmodel.encapsulate;


import io.beanmapper.annotations.BeanProperty;

public class ResultManyToMany {

    private String name;
    @BeanProperty(name = "address")
    private ResultAddress addressOfTheHouse;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResultAddress getAddressOfTheHouse() {
        return addressOfTheHouse;
    }

    public void setAddressOfTheHouse(ResultAddress addressOfTheHouse) {
        this.addressOfTheHouse = addressOfTheHouse;
    }
}
