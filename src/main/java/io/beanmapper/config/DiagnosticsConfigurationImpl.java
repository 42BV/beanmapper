package io.beanmapper.config;

import java.util.Optional;

import io.beanmapper.utils.diagnostics.DiagnosticsDetailLevel;
import io.beanmapper.utils.diagnostics.logging.DiagnosticsLogger;
import io.beanmapper.utils.diagnostics.tree.DiagnosticsNode;
import io.beanmapper.utils.diagnostics.tree.MappingDiagnosticsNode;

public class DiagnosticsConfigurationImpl extends OverrideConfiguration {

    private final DiagnosticsLogger logger;
    private DiagnosticsNode<?, ?> diagnostics;

    public DiagnosticsConfigurationImpl(Configuration configuration, DiagnosticsDetailLevel detailLevel) {
        super(configuration);
        this.setTarget(configuration.getTarget());
        this.setTargetClass(configuration.getTargetClass());
        this.setApplyStrictMappingConvention(configuration.isApplyStrictMappingConvention());
        this.setCollectionClass(configuration.getCollectionClass());
        this.logger = detailLevel.getLogger();
        this.diagnostics = new MappingDiagnosticsNode<>(Void.class, configuration.getTargetClass());
    }

    @Override
    public <S, T> void setBeanMapperDiagnostics(DiagnosticsNode<S, T> diagnostics) {
        this.diagnostics = diagnostics;
    }

    @Override
    public boolean isInDiagnosticsMode() {
        return true;
    }

    @Override
    public int getMappingDepth() {
        return 0;
    }

    @Override
    public <S, T> Optional<DiagnosticsNode<S, T>> getBeanMapperDiagnostics() {
        return Optional.of((DiagnosticsNode<S, T>) diagnostics);
    }

    @Override
    public Optional<DiagnosticsLogger> getDiagnosticsLogger() {
        return Optional.ofNullable(logger);
    }

}
