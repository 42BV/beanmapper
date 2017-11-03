package io.beanmapper.testmodel.strict_convention;

public class SCSourceAForm {

    public String name;

    private String doesNotExist;

    public void setDoesNotExist(String doesNotExist) {
        this.doesNotExist = doesNotExist;
    }

    public String getDoesExist() {
        return this.doesNotExist;
    }

}
