package io.beanmapper.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.beanmapper.config.BeanPair;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;

public class BeanMatch {

    private final BeanPair beanPair;

    private final Map<String, BeanProperty> sourceNodes;

    private final Map<String, BeanProperty> targetNodes;

    private final Map<String, BeanProperty> aliases;

    private boolean verified = false;

    public BeanMatch(BeanPair beanPair, Map<String, BeanProperty> sourceNodes, Map<String, BeanProperty> targetNodes, Map<String, BeanProperty> aliases) {
        this.beanPair = beanPair;
        this.sourceNodes = sourceNodes != null ? sourceNodes : Collections.emptyMap();
        this.targetNodes = targetNodes != null ? targetNodes : Collections.emptyMap();
        this.aliases = aliases != null ? aliases : Collections.emptyMap();
        validateMappingRequirements();
    }

    public Class<?> getSourceClass() {
        return beanPair.getSourceClass();
    }

    public Class<?> getTargetClass() {
        return beanPair.getTargetClass();
    }

    public Map<String, BeanProperty> getSourceNodes() {
        return this.sourceNodes != null ? this.sourceNodes : Collections.emptyMap();
    }

    public Map<String, BeanProperty> getTargetNodes() {
        return this.targetNodes != null ? this.targetNodes : Collections.emptyMap();
    }

    public Map<String, BeanProperty> getAliases() {
        return this.aliases != null ? this.aliases : Collections.emptyMap();
    }

    public MatchedBeanPropertyPair findBeanPairField(String fieldName) {
        BeanProperty sourceField = getSourceNodes().get(fieldName);
        if(sourceField == null) {
            // No source field found -> check for alias
            sourceField = getAliases().get(fieldName);
        }
        BeanProperty targetField = getTargetNodes().get(fieldName);
        if(targetField == null) {
            // No target field found -> check for alias
            targetField = getAliases().get(fieldName);
        }
        return new MatchedBeanPropertyPair(sourceField, targetField);
    }

    public void validateMappingRequirements() {
        if (beanPair.isSourceStrict()) {
            validateBeanMatchValidity(sourceNodes);
        } else if (beanPair.isTargetStrict()) {
            validateBeanMatchValidity(targetNodes);
        }
    }

    private void validateBeanMatchValidity(Map<String, BeanProperty> nodes) {
        List<BeanProperty> missingMatches = validateMappingRequirements(nodes);
        if (!missingMatches.isEmpty()) {
            throw new BeanStrictMappingRequirementsException(
                    new BeanMatchValidationMessage(beanPair, missingMatches));
        }
    }

    private List<BeanProperty> validateMappingRequirements(Map<String, BeanProperty> nodes) {
        List<BeanProperty> missingMatches = new ArrayList<>();
        for (String fieldName : nodes.keySet()) {
            MatchedBeanPropertyPair matchedField = findBeanPairField(fieldName);
            BeanProperty sourceBeanProperty = matchedField.sourceBeanProperty();
            BeanProperty targetBeanProperty = matchedField.targetBeanProperty();
            if (    sourceBeanProperty == null || targetBeanProperty == null) {
                missingMatches.add(sourceBeanProperty == null ?
                        targetBeanProperty :
                        sourceBeanProperty);
            }
        }
        return missingMatches;
    }

    public void checkForMandatoryUnmatchedNodes() {
        if (verified) {
            return;
        }
        verified = true;
        checkForMandatoryUnmatchedNodes("source", beanPair.getSourceClass(), sourceNodes);
        checkForMandatoryUnmatchedNodes("target", beanPair.getTargetClass(), targetNodes);
    }

    private void checkForMandatoryUnmatchedNodes(String side, Class<?> containingClass, Map<String, BeanProperty> nodes) {
        for (Map.Entry<String, BeanProperty> entry : nodes.entrySet()) {
            BeanProperty currentField = entry.getValue();
            if (currentField.isUnmatched()) {
                throw new BeanNoSuchPropertyException(side + " " + containingClass.getCanonicalName() + " has no match for property " + entry.getKey());
            }
        }
    }

}
