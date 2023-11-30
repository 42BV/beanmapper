package io.beanmapper.routine;

public final class Routines {

    private static final Routine<?, ?> NO_OP_ROUTINE = input -> input;

    private static final Routine<?, String> TO_STRING_ROUTINE = Object::toString;

    @SuppressWarnings("unchecked")
    public static <S> Routine<S, S> noOpRoutine() {
        return (Routine<S, S>) NO_OP_ROUTINE;
    }

    @SuppressWarnings("unchecked")
    public static <S> Routine<S, String> toStringRoutine() {
        return (Routine<S, String>) TO_STRING_ROUTINE;
    }
}
