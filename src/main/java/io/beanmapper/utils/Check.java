/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.utils;

/**
 * Performs assertions in code.
 *
 * @author Jeroen van Schagen
 * @since Jun 24, 2015
 */
public class Check {
    
    public static void argument(boolean condition, String message) {
        if (!condition) {
            throw new IllegalArgumentException(message);
        }
    }

}
