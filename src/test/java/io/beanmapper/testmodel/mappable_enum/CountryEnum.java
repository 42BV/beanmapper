package io.beanmapper.testmodel.mappable_enum;

import io.beanmapper.annotations.BeanMappableEnum;

@BeanMappableEnum
public enum CountryEnum {
    NL("Netherlands", "dutch-flag.png"),
    US("United States", "american-flag.png");

    private final String displayName;
    private final String image;

    CountryEnum(String displayName, String image) {
        this.displayName = displayName;
        this.image = image;
    }

    public CountryEnum getCode() {
        return this;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getImage() {
        return image;
    }
}
