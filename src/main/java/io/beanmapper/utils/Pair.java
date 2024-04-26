package io.beanmapper.utils;

import java.util.Objects;

public record Pair<S, T>(S first, T second) {

    public static final Pair <?, ?> EMPTY_PAIR = Pair.of(null, null);

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Pair<?, ?> other) {
            return Objects.equals(first, other.first) && Objects.equals(second, other.second);
        }
        return false;
    }

    public static <S, T> Pair<S, T> of(S first, T second) {
        return new Pair<>(first, second);
    }
}
