package io.beanmapper.annotations;

/**
 * An enum used to set whether a constructor of a Record should be considered for mapping, force to be used for mapping,
 * or excluded from mapping.
 *
 * @see RecordConstruct
 */
public enum RecordConstructMode {

    ON_DEMAND,
    FORCE,
    EXCLUDE

}
