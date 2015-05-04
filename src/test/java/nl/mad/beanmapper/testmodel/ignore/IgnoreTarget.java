package nl.mad.beanmapper.testmodel.ignore;

import nl.mad.beanmapper.annotations.BeanIgnore;

public class IgnoreTarget {

    @BeanIgnore
    private String bothIgnore;
    private String sourceIgnore;
    @BeanIgnore
    private String targetIgnore;
    private String noIgnore;

    public String getNoIgnore() {
        return noIgnore;
    }

    public void setNoIgnore(String noIgnore) {
        this.noIgnore = noIgnore;
    }

    public String getBothIgnore() {
        return bothIgnore;
    }

    public void setBothIgnore(String bothIgnore) {
        this.bothIgnore = bothIgnore;
    }

    public String getSourceIgnore() {
        return sourceIgnore;
    }

    public void setSourceIgnore(String sourceIgnore) {
        this.sourceIgnore = sourceIgnore;
    }

    public String getTargetIgnore() {
        return targetIgnore;
    }

    public void setTargetIgnore(String targetIgnore) {
        this.targetIgnore = targetIgnore;
    }
}
