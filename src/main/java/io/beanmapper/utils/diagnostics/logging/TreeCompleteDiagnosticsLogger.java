package io.beanmapper.utils.diagnostics.logging;

import static io.beanmapper.utils.diagnostics.logging.StackTraceProcessor.getFormattedCalledLink;

import java.util.logging.Logger;

import io.beanmapper.utils.Pair;
import io.beanmapper.utils.diagnostics.tree.DiagnosticsNode;

public final class TreeCompleteDiagnosticsLogger implements DiagnosticsLogger {

    private static final String INDENT = "  ";
    private final Logger logger;

    public TreeCompleteDiagnosticsLogger(Logger logger) {
        this.logger = logger;
    }

    @Override
    public <S, T> void log(DiagnosticsNode<S, T> root) {
        logLayer(root, 0);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    private <S, T> void logLayer(DiagnosticsNode<S, T> node, int layer) {
        if (node.getTypes() == null || node.getTypes().equals(Pair.EMPTY_PAIR)) {
            node.getDiagnostics().forEach(child -> logLayer(child, layer));
            return;
        }

        logConversion(logger, node, layer);
        node.getDiagnostics().forEach(n -> logLayer(n, layer + 1));
    }

    private <S, T, D extends DiagnosticsNode<S, T>> void logConversion(Logger logger, D info, int layer) {
        logger.info("%s%s %s".formatted(INDENT.repeat(layer), info.toString(), layer == 0 ? getFormattedCalledLink() : ""));
    }
}
