/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.testmodel.person;

/**
 * Person active objects interface.
 *
 * @author Jeroen van Schagen
 * @since Jun 19, 2015
 */
public interface PersonAo {

    Long getId();

    void setId(Long id);

    String getName();

    void setName(String name);

    String getPlace();

    void setPlace(String place);

    // Should be ignored as it is not known in the target
    String getUnknownProperty();

}
