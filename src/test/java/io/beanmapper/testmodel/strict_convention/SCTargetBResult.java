package io.beanmapper.testmodel.strict_convention;

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
