package io.beanmapper.testmodel.beanalias;

import io.beanmapper.annotations.BeanProperty;

public class TargetWithAlias {

    public int aliasId;
    @BeanProperty(name = "aliasName")
    public String targetName;
    public NestedTargetWithAlias nestedWithAlias;
}
