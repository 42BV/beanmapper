package io.beanmapper.testmodel.enums;

public enum RGB {

    RED("Red"),
    GREEN("Green"),
    BLUE("Blue");

    private final String color;

    RGB(String color) {
        this.color = color;
    }

    public String RGB() {
        return color;
    }

}