package io.beanmapper.core;

import java.util.Objects;

public class BeanPropertyWrapper {
    private String name;
    private boolean mustMatch = false;

    public BeanPropertyWrapper(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isMustMatch() {
        return mustMatch;
    }

    public void setMustMatch() {
        this.mustMatch = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;

        BeanPropertyWrapper that = (BeanPropertyWrapper) obj;

        if (mustMatch != that.mustMatch)
            return false;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, mustMatch);
    }
}
