package io.beanmapper.core;

public class BeanPropertyWrapper {
    private String name;
    private boolean mustMatch = false;

    public BeanPropertyWrapper(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isMustMatch() {
        return mustMatch;
    }

    public void setMustMatch() {
        this.mustMatch = true;
    }
}
