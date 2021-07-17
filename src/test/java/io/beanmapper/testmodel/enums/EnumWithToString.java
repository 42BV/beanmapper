package io.beanmapper.testmodel.enums;

public enum EnumWithToString {
    SOME_VALUE;

    public String toString() {
        return "X" + name() + "X";
    }
}
