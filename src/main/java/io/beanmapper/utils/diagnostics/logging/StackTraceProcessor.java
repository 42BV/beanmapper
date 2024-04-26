package io.beanmapper.utils.diagnostics.logging;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class StackTraceProcessor {
    private static final String CLASS_PREFIX = "io.beanmapper.";

    private StackTraceProcessor() {
        throw new AssertionError("This class should not be instantiated.");
    }

    public static String getFormattedCalledLink() {
        List<StackTraceElement> stackTraceElements = getStackTraceElements();
        StackTraceElement suitableElement = getFirstSuitableElement(stackTraceElements);
        return suitableElement != null ? formatLink(suitableElement) : null;
    }

    private static List<StackTraceElement> getStackTraceElements() {
        List<StackTraceElement> stackTraceElements = Arrays.asList(Thread.currentThread().getStackTrace());
        Collections.reverse(stackTraceElements);
        return stackTraceElements;
    }

    private static StackTraceElement getFirstSuitableElement(List<StackTraceElement> stackTraceElements) {
        for (int i = 0; i < stackTraceElements.size(); i++) {
            StackTraceElement stackTraceElement = stackTraceElements.get(i + 1);
            if (stackTraceElement.getClassName().startsWith(CLASS_PREFIX) && stackTraceElement.getMethodName().equals("map")) {
                return stackTraceElements.get(i);
            }
        }
        return null;
    }

    private static String formatLink(StackTraceElement element) {
        return "(%s:%s)".formatted(element.getFileName(), element.getLineNumber());
    }
}
