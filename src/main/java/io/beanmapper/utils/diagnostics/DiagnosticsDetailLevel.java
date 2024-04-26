package io.beanmapper.utils.diagnostics;

import java.util.logging.Logger;

import io.beanmapper.utils.diagnostics.logging.CountPerPairDiagnosticsLogger;
import io.beanmapper.utils.diagnostics.logging.CountTotalDiagnosticsLogger;
import io.beanmapper.utils.diagnostics.logging.DiagnosticsLogger;
import io.beanmapper.utils.diagnostics.logging.TreeCompleteDiagnosticsLogger;

/**
 * DiagnosticsDetailLevel is an enumeration class that represents different levels of detail for diagnostic logging.
 * Each level is associated with a specific DiagnosticsLogger implementation.
 */
public enum DiagnosticsDetailLevel {

    COUNT_TOTAL(new CountTotalDiagnosticsLogger(Logger.getLogger(CountTotalDiagnosticsLogger.class.getName())), true),
    COUNT_PER_PAIR(new CountPerPairDiagnosticsLogger(Logger.getLogger(CountPerPairDiagnosticsLogger.class.getName())), true),
    TREE_COMPLETE(new TreeCompleteDiagnosticsLogger(Logger.getLogger(TreeCompleteDiagnosticsLogger.class.getName())), true),
    DISABLED(null, false);

    private final DiagnosticsLogger logger;
    private final boolean enabled;

    DiagnosticsDetailLevel(DiagnosticsLogger logger, boolean enabled) {
        this.logger = logger;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public DiagnosticsLogger getLogger() {
        return logger;
    }
}
