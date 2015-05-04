package nl.mad.beanmapper.testmodel.othername;

import nl.mad.beanmapper.annotations.BeanProperty;

public class TargetWithOtherName {

    @BeanProperty(name = "bothOtherName")
    private String bothOtherName2;
    private String sourceOtherName;
    @BeanProperty(name = "targetOtherName")
    private String targetOtherName1;
    private String noOtherName;

    public String getBothOtherName2() {
        return bothOtherName2;
    }

    public void setBothOtherName2(String bothOtherName2) {
        this.bothOtherName2 = bothOtherName2;
    }

    public String getSourceOtherName() {
        return sourceOtherName;
    }

    public void setSourceOtherName(String sourceOtherName) {
        this.sourceOtherName = sourceOtherName;
    }

    public String getTargetOtherName1() {
        return targetOtherName1;
    }

    public void setTargetOtherName1(String targetOtherName1) {
        this.targetOtherName1 = targetOtherName1;
    }

    public String getNoOtherName() {
        return noOtherName;
    }

    public void setNoOtherName(String noOtherName) {
        this.noOtherName = noOtherName;
    }
}
