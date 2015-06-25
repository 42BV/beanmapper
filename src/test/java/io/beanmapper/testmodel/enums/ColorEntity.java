package io.beanmapper.testmodel.enums;

/**
 * Created by kevinwareman on 25-06-15.
 */
public class ColorEntity {

    public enum RGB {
        RED("Red"),
        GREEN("Green"),
        BLUE("Blue");

        private final String color;

        RGB(String color){
            this.color = color;
        }

        public String RGB() {
            return color;
        }
    }

    private RGB currentColor;

    public RGB getCurrentColor() {
        return currentColor;
    }

    public void setCurrentColor(RGB currentColor) {
        this.currentColor = currentColor;
    }
}
