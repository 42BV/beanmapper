package io.beanmapper.testmodel.strictconvention;

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
