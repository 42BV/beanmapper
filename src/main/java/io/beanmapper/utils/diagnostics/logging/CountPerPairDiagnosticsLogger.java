package io.beanmapper.utils.diagnostics.logging;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import io.beanmapper.utils.Pair;
import io.beanmapper.utils.diagnostics.tree.ConversionDiagnosticsNode;
import io.beanmapper.utils.diagnostics.tree.DiagnosticsNode;
import io.beanmapper.utils.diagnostics.tree.MappingDiagnosticsNode;

/**
 * An implementation of the DiagnosticLogger, responsible for logging the exact amount of mappings and conversions performed per source-target pair.
 */
public final class CountPerPairDiagnosticsLogger implements DiagnosticsLogger {

    private final Logger logger;

    public CountPerPairDiagnosticsLogger(Logger logger) {
        this.logger = logger;
    }

    public Logger getLogger() {
        return logger;
    }

    @Override
    public <S, T> void log(DiagnosticsNode<S, T> root) {
        Map<DiagnosticsNode<?, ?>, Pair<AtomicInteger, AtomicInteger>> diagnosticSummary = calculateDiagnostics(root, new HashMap<>());

        logger.info(String.format("[Source    ]: %s", root.toString().substring(13)));

        for (Map.Entry<DiagnosticsNode<?, ?>, Pair<AtomicInteger, AtomicInteger>> entry : diagnosticSummary.entrySet()) {
            String keyString = entry.getKey().toString();
            int value = getValueBasedOnKeyType(entry);
            logger.info(String.format("  %s : %d", keyString, value));
        }
    }

    private int getValueBasedOnKeyType(Map.Entry<DiagnosticsNode<?, ?>, Pair<AtomicInteger, AtomicInteger>> entry) {
        if (entry.getKey() instanceof MappingDiagnosticsNode) {
            return entry.getValue().first().get();
        }
        return entry.getValue().second().get();
    }

    private <S, T> void incrementPairValue(Map<DiagnosticsNode<?, ?>, Pair<AtomicInteger, AtomicInteger>> reference,
            DiagnosticsNode<S, T> node,
            boolean isFirst) {
        reference.computeIfAbsent(node, key -> Pair.of(new AtomicInteger(0), new AtomicInteger(0))).first().addAndGet(isFirst ? 1 : 0);
        reference.computeIfAbsent(node, key -> Pair.of(new AtomicInteger(0), new AtomicInteger(0))).second().addAndGet(isFirst ? 0 : 1);
    }

    private <S, T> Map<DiagnosticsNode<?, ?>, Pair<AtomicInteger, AtomicInteger>> calculateDiagnostics(DiagnosticsNode<S, T> node,
            Map<DiagnosticsNode<?, ?>, Pair<AtomicInteger, AtomicInteger>> existing) {
        var reference = new AtomicReference<>(existing);

        incrementPairValue(reference.get(), node, MappingDiagnosticsNode.class.isAssignableFrom(node.getClass()));
        incrementPairValue(reference.get(), node, ConversionDiagnosticsNode.class.isAssignableFrom(node.getClass()));
        node.getDiagnostics().forEach(c2 -> reference.set(calculateDiagnostics(c2, reference.get())));

        return reference.get();
    }
}
