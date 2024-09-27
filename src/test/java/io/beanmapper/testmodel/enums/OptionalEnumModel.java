package io.beanmapper.testmodel.enums;

import java.util.Optional;

public class OptionalEnumModel {

    private Optional<Day> day;

    public OptionalEnumModel(Day day) {
        this.day = Optional.ofNullable(day);
    }

    public Optional<Day> getDay() {
        return day;
    }
}
