package io.beanmapper.utils.diagnostics.logging;

import static io.beanmapper.utils.diagnostics.logging.StackTraceProcessor.getFormattedCalledLink;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.ToIntFunction;
import java.util.logging.Logger;

import io.beanmapper.utils.Pair;
import io.beanmapper.utils.diagnostics.tree.DiagnosticsNode;

public final class CountTotalDiagnosticsLogger implements DiagnosticsLogger {

    private final Logger logger;

    public CountTotalDiagnosticsLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public <S, T> void log(DiagnosticsNode<S, T> root) {
        var totalCount = calculateTotalCount(root);
        int maxDepth = determineMaxDepth(root, 0);
        logger.info("%s {total mappings: %d, total conversions: %d, max depth: %d} %s".formatted(root.toString(), totalCount.first().get() + 1,
                totalCount.second().get(), maxDepth + 1, getFormattedCalledLink()));
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private <S, T> Pair<AtomicInteger, AtomicInteger> calculateTotalCount(DiagnosticsNode<S, T> currentNode) {
        List<?> mappings = currentNode.getMappingDiagnostics();
        List<?> conversions = currentNode.getConversionDiagnostics();
        AtomicInteger mappingsTotal = new AtomicInteger(mappings.size());
        AtomicInteger conversionsTotal = new AtomicInteger(conversions.size());

        processChildren(currentNode, child -> {
            var pair = calculateTotalCount(child);
            mappingsTotal.addAndGet(pair.first().get());
            conversionsTotal.addAndGet(pair.second().get());
        });

        return Pair.of(mappingsTotal, conversionsTotal);
    }

    private <S, T> int determineMaxDepth(DiagnosticsNode<S, T> currentNode, int depth) {
        return processChildren(currentNode, child -> determineMaxDepth(child, depth + 1), depth);
    }

    private <S, T> void processChildren(DiagnosticsNode<S, T> currentNode, Consumer<? super DiagnosticsNode<?, ?>> function) {
        currentNode.getDiagnostics().forEach(function);
    }

    private <S, T> int processChildren(DiagnosticsNode<S, T> currentNode, ToIntFunction<? super DiagnosticsNode<?, ?>> function, int defaultValue) {
        return currentNode.getDiagnostics().stream()
                .mapToInt(function)
                .max()
                .orElse(defaultValue);
    }
}
