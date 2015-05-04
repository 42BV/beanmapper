package nl.mad.beanmapper.testmodel.defaults;

import nl.mad.beanmapper.annotations.BeanDefault;

public class TargetWithDefaults {

    @BeanDefault("bothdefault2")
    private String bothDefault;
    private String sourceDefault;
    @BeanDefault("targetdefault")
    private String targetDefault;
    private String noDefault;
    @BeanDefault("targetdefaultwithoutmatch")
    private String targetDefaultWithoutMatch;
    @BeanDefault("Dit veld heeft al een waarde")
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

    public String getTargetDefaultWithoutMatch() {
        return targetDefaultWithoutMatch;
    }

    public void setTargetDefaultWithoutMatch(String targetDefaultWithoutMatch) {
        this.targetDefaultWithoutMatch = targetDefaultWithoutMatch;
    }

    public String getTargetDefaultWithValue() {
        return targetDefaultWithValue;
    }

    public void setTargetDefaultWithValue(String targetDefaultWithValue) {
        this.targetDefaultWithValue = targetDefaultWithValue;
    }
}
