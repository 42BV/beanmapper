package io.beanmapper.exceptions;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;

public final class RecordConstructorConflictException extends RuntimeException {

    public <T> RecordConstructorConflictException(final Collection<Constructor<T>> constructors) {
        this(() -> {
            StringBuilder builder = new StringBuilder("Record class ")
                    .append(constructors.iterator().next().getDeclaringClass().getName())
                    .append(" contains ")
                    .append(constructors.size())
                    .append(" constructors annotated with @RecordConstruct(String[], RecordConstructMode.FORCE).\n")
                    .append("Signatures of offending constructors:\n");
            for (var constructor : constructors) {
                builder.append("\t- ")
                        .append(constructor)
                        .append("\n");
            }
            return builder.toString();
        });
    }

    private RecordConstructorConflictException(final Supplier<String> messageSupplier) {
        super(messageSupplier.get());
    }

}
