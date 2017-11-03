package io.beanmapper.testmodel.beanalias;

import io.beanmapper.annotations.BeanAlias;

public class SourceWithAlias {

    @BeanAlias("aliasId")
    public int id;
    @BeanAlias("aliasName")
    public String sourceName;
    public NestedSourceWithAlias nestedWithAlias;
}

