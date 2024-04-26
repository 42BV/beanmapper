package io.beanmapper.utils.diagnostics.logging;

import java.util.logging.Logger;

import io.beanmapper.utils.diagnostics.tree.DiagnosticsNode;

public sealed interface DiagnosticsLogger permits CountTotalDiagnosticsLogger, CountPerPairDiagnosticsLogger, TreeCompleteDiagnosticsLogger {

    <S, T> void log(DiagnosticsNode<S, T> root);

    Logger getLogger();
}
