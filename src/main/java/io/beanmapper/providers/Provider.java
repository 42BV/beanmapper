package io.beanmapper.providers;

public interface Provider {

    /**
     * Get the default value given some S type.
     *
     * @param source {@link Class} to get the default value of.
     * @return the default value given the type.
     */
    <T extends S, S> T get(Class<S> source);
}