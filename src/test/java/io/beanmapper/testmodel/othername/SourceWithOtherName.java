package io.beanmapper.testmodel.othername;

import io.beanmapper.annotations.BeanProperty;

public class SourceWithOtherName {

    @BeanProperty(name = "bothOtherName")
    private String bothOtherName1;
    @BeanProperty(name = "sourceOtherName")
    private String sourceOtherName1;
    private String targetOtherName;
    private String noOtherName;

    public String getBothOtherName1() {
        return bothOtherName1;
    }

    public void setBothOtherName1(String bothOtherName1) {
        this.bothOtherName1 = bothOtherName1;
    }

    public String getSourceOtherName1() {
        return sourceOtherName1;
    }

    public void setSourceOtherName1(String sourceOtherName1) {
        this.sourceOtherName1 = sourceOtherName1;
    }

    public String getTargetOtherName() {
        return targetOtherName;
    }

    public void setTargetOtherName(String targetOtherName) {
        this.targetOtherName = targetOtherName;
    }

    public String getNoOtherName() {
        return noOtherName;
    }

    public void setNoOtherName(String noOtherName) {
        this.noOtherName = noOtherName;
    }
}
