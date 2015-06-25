/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.testmodel.project;

import io.beanmapper.annotations.BeanProperty;

/**
 * 
 *
 * @author jeroen
 * @since Jun 25, 2015
 */
public class CodeProjectResult {
    
    public Long id;
    
    public final String name = null;
    
    @BeanProperty(name = "master.id")
    public Long masterId;
    
    @BeanProperty(name = "master.name")
    private String masterName = null;
    
    public boolean isMaster() {
        return masterId != null;
    }

    public String getMasterName() {
        return masterName;
    }

}
