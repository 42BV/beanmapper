package io.beanmapper.annotations;

/**
 * An enum used to set whether a constructor of a Record should be considered for mapping, force to be used for mapping,
 * or excluded from mapping.
 *
 * @see BeanRecordConstruct
 */
public enum BeanRecordConstructMode {

    ON_DEMAND,
    FORCE,
    EXCLUDE

}
