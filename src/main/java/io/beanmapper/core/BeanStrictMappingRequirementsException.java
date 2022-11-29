package io.beanmapper.core;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanStrictMappingRequirementsException extends RuntimeException {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final Collection<BeanMatchValidationMessage> validationMessages;

    public BeanStrictMappingRequirementsException(BeanMatchValidationMessage validationMessage) {
        this(Collections.<BeanMatchValidationMessage>singletonList(validationMessage));
    }

    public BeanStrictMappingRequirementsException(Collection<BeanMatchValidationMessage> validationMessages) {
        this.validationMessages = validationMessages;
        logErrors(validationMessages);
    }

    private void logErrors(Collection<BeanMatchValidationMessage> validationMessages) {
        for (BeanMatchValidationMessage validationMessage : validationMessages) {
            if (validationMessage.isLogged()) {
                continue;
            }
            logger.error("""
                            Missing matching properties for source [{}] {} > target [{}] {} for fields:
                            """,
                    validationMessage.getSourceClass().getCanonicalName(),
                    (validationMessage.isSourceStrict() ? "*" : ""),
                    validationMessage.getTargetClass().getCanonicalName(),
                    (validationMessage.isTargetStrict() ? "*" : ""));

            for (BeanProperty field : validationMessage.getFields()) {
                logger.error("""
                                > {}.{}
                                """,
                        validationMessage.getStrictClass().getSimpleName(),
                        field.getAccessor().getName());
            }
            validationMessage.setLogged();
        }
    }

    public Collection<BeanMatchValidationMessage> getValidationMessages() {
        return this.validationMessages != null ? this.validationMessages : Collections.emptyList();
    }

}
