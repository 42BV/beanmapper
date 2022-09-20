package io.beanmapper.core.collections;

import java.util.Objects;

/**
 * An interface that may be implemented by a class, to make the class compatible with BeanMapper#map(Collection, Collection) and BeanMapper#map(Map, Map).
 */
public interface Equalizer {

    /**
     * Tests whether the two classes are equal, for the purposes of mapping the calling object, to the target object. Default implementation should be
     * overridden for every implementing class.
     * @param target the target instance, to which the calling instance is looking to be mapped.
     * @return whether the caller and target are equal.
     * @param <T> the class of the target
     */
    default <T extends Equalizer> boolean isEqual(T target) {
        return Objects.equals(this, target);
    }
}
