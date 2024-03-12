package io.beanmapper.testmodel.enums;

public enum EnumWithOverride {
    DOG("Dog"),
    CAT("Cat"),
    HORSE("Horse") {
        @Override
        public String getLabel() {
            return "KETENREF-%s".formatted(this.name());
        }
    },
    HAMSTER("Hamster");

    private final String label;

    EnumWithOverride(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}