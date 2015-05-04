package io.beanmapper.testmodel.defaults;

import io.beanmapper.annotations.BeanDefault;

public class SourceWithDefaults {

    @BeanDefault(value = "bothdefault1")
    private String bothDefault;
    @BeanDefault(value = "sourcedefault")
    private String sourceDefault;
    private String targetDefault;
    private String noDefault;
    private String targetDefaultWithValue;

    public String getBothDefault() {
        return bothDefault;
    }

    public void setBothDefault(String bothDefault) {
        this.bothDefault = bothDefault;
    }

    public String getSourceDefault() {
        return sourceDefault;
    }

    public void setSourceDefault(String sourceDefault) {
        this.sourceDefault = sourceDefault;
    }

    public String getTargetDefault() {
        return targetDefault;
    }

    public void setTargetDefault(String targetDefault) {
        this.targetDefault = targetDefault;
    }

    public String getNoDefault() {
        return noDefault;
    }

    public void setNoDefault(String noDefault) {
        this.noDefault = noDefault;
    }

    public String getTargetDefaultWithValue() {
        return targetDefaultWithValue;
    }

    public void setTargetDefaultWithValue(String targetDefaultWithValue) {
        this.targetDefaultWithValue = targetDefaultWithValue;
    }
}
