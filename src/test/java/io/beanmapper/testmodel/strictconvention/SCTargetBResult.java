package io.beanmapper.testmodel.strictconvention;

public class SCTargetBResult {

    public String name;
    private String doesNotExist;

    public void setDoesExist(String doesExist) {
        this.doesNotExist = doesExist;
    }

    public String getDoesNotExist() {
        return doesNotExist;
    }
}
