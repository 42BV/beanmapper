package io.beanmapper.core;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BeanStrictMappingRequirementsException extends RuntimeException {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<BeanMatchValidationMessage> validationMessages;

    public BeanStrictMappingRequirementsException(BeanMatchValidationMessage validationMessage) {
        this(Collections.singletonList(validationMessage));
    }

    public BeanStrictMappingRequirementsException(List<BeanMatchValidationMessage> validationMessages) {
        super(
          "One or more property mismatches between a mapped source and target type. " +
          "Read the error logging above for more details."
        );

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
            for (BeanProperty field : validationMessage.getFields()) {
                logger.error(
                        "> " +
                        validationMessage.getStrictClass().getSimpleName() +
                        "." +
                        field.getAccessor().getName());
            }
            validationMessage.setLogged();
        }
    }

    public List<BeanMatchValidationMessage> getValidationMessages() {
        return validationMessages;
    }

}
