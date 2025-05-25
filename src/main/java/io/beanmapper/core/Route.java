package io.beanmapper.core;

public record Route(String[] route) {

    public Route(String path) {
        this(path == null ? new String[0] : path.split("\\."));
    }

}
