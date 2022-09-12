package io.beanmapper.core.collections;

public class ComparableClass implements Comparable {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        if (this == o)
            return 0;
        if (!(o instanceof ComparableClass)) {
            return -1;
        }
        ComparableClass other = (ComparableClass) o;
        return name.compareTo(other.name);
    }

    @Override
    public boolean equals(Object o) {
        return compareTo(o) == 0;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
