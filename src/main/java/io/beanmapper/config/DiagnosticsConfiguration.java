package io.beanmapper.config;

import java.util.Optional;

import io.beanmapper.utils.diagnostics.DiagnosticsDetailLevel;
import io.beanmapper.utils.diagnostics.logging.DiagnosticsLogger;
import io.beanmapper.utils.diagnostics.tree.DiagnosticsNode;

public interface DiagnosticsConfiguration extends Configuration {

    boolean isInDiagnosticsMode();

    <S, T> Optional<DiagnosticsNode<S, T>> getBeanMapperDiagnostics();

    <S, T> void setBeanMapperDiagnostics(DiagnosticsNode<S, T> diagnostics);

    default Optional<DiagnosticsLogger> getDiagnosticsLogger(){
        return Optional.empty();
    }

    int getMappingDepth();

    DiagnosticsDetailLevel getDiagnosticsDetailLevel();

}
