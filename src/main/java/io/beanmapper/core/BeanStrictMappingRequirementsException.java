package io.beanmapper.core;

import java.util.Collections;
import java.util.List;

import io.beanmapper.utils.BeanMapperTraceLogger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanStrictMappingRequirementsException extends RuntimeException {

    private static final Logger log = LoggerFactory.getLogger(BeanStrictMappingRequirementsException.class);
    private final List<BeanMatchValidationMessage> validationMessages;

    public BeanStrictMappingRequirementsException(BeanMatchValidationMessage validationMessage) {
        this(Collections.<BeanMatchValidationMessage>singletonList(validationMessage));
    }

    public BeanStrictMappingRequirementsException(List<BeanMatchValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
        logErrors(validationMessages);
    }

    private void logErrors(List<BeanMatchValidationMessage> validationMessages) {
        for (BeanMatchValidationMessage validationMessage : validationMessages) {
            if (validationMessage.isLogged()) {
                continue;
            }
            log.error("""
                            Missing matching properties for source [{}] {} > target [{}] {} for fields:
                            """,
                    validationMessage.getSourceClass().getCanonicalName(),
                    (validationMessage.isSourceStrict() ? "*" : ""),
                    validationMessage.getTargetClass().getCanonicalName(),
                    (validationMessage.isTargetStrict() ? "*" : ""));

            for (BeanProperty field : validationMessage.getFields()) {
                log.error("""
                                > {}.{}
                                """,
                        validationMessage.getStrictClass().getSimpleName(),
                        field.getAccessor().getName());
            }
            validationMessage.setLogged();
        }
    }

    public List<BeanMatchValidationMessage> getValidationMessages() {
        return this.validationMessages != null ? this.validationMessages : Collections.emptyList();
    }

}
