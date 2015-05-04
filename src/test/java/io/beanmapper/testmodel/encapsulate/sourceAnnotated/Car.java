package io.beanmapper.testmodel.encapsulate.sourceAnnotated;

public class Car {

    private String brand;
    private int wheels;

    public Car(String brand, int wheels) {
        this.brand = brand;
        this.wheels = wheels;
    }

    public int getWheels() {
        return wheels;
    }

    public void setWheels(int wheels) {
        this.wheels = wheels;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
