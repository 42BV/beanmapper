package io.beanmapper.core;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanStrictMappingRequirementsException extends RuntimeException {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

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
            logger.error(
                    "Missing matching properties for source [" +
                    validationMessage.getSourceClass().getCanonicalName() +
                    "]" +
                    (validationMessage.isSourceStrict() ? "*" : "") +
                    " > target [" +
                    validationMessage.getTargetClass().getCanonicalName() +
                    "]" +
                    (validationMessage.isTargetStrict() ? "*" : "") +
                    " for fields:"
            );
            for (BeanField field : validationMessage.getFields()) {
                logger.error(
                        "> " +
                        validationMessage.getStrictClass().getSimpleName() +
                        "." +
                        field.getProperty().getName());
            }
            validationMessage.setLogged();
        }
    }

    public List<BeanMatchValidationMessage> getValidationMessages() {
        return validationMessages;
    }

}
