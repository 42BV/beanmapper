package io.beanmapper.testmodel.ignore;

import io.beanmapper.annotations.BeanIgnore;

public class IgnoreSource {

    @BeanIgnore
    private String bothIgnore;
    @BeanIgnore
    private String sourceIgnore;
    private String targetIgnore;
    private String noIgnore;

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

    public String getNoIgnore() {
        return noIgnore;
    }

    public void setNoIgnore(String noIgnore) {
        this.noIgnore = noIgnore;
    }
}
