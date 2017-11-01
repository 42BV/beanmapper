package io.beanmapper.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.beanmapper.config.BeanPair;
import io.beanmapper.exceptions.BeanNoSuchPropertyException;

public class BeanMatch {

    private final BeanPair beanPair;

    private final Map<String, BeanField> sourceNode;

    private final Map<String, BeanField> targetNode;

    private final Map<String, BeanField> aliases;

    private boolean verified = false;

    public BeanMatch(BeanPair beanPair, Map<String, BeanField> sourceNode, Map<String, BeanField> targetNode, Map<String, BeanField> aliases) {
        this.beanPair = beanPair;
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.aliases = aliases;
        validateMappingRequirements();
    }

    public Class<?> getSourceClass() {
        return beanPair.getSourceClass();
    }

    public Class<?> getTargetClass() {
        return beanPair.getTargetClass();
    }

    public Map<String, BeanField> getSourceNode() {
        return sourceNode;
    }

    public Map<String, BeanField> getTargetNode() {
        return targetNode;
    }

    public Map<String, BeanField> getAliases() {
        return aliases;
    }

    public MatchedBeanPairField findBeanPairField(String fieldName) {
        BeanField sourceField = getSourceNode().get(fieldName);
        if(sourceField == null) {
            // No source field found -> check for alias
            sourceField = getAliases().get(fieldName);
        }
        BeanField targetField = getTargetNode().get(fieldName);
        if(targetField == null) {
            // No target field found -> check for alias
            targetField = getAliases().get(fieldName);
        }
        return new MatchedBeanPairField(sourceField, targetField);
    }

    public void validateMappingRequirements() {
        if (beanPair.isSourceStrict()) {
            validateBeanMatchValidity(sourceNode);
        } else if (beanPair.isTargetStrict()) {
            validateBeanMatchValidity(targetNode);
        }
    }

    private void validateBeanMatchValidity(Map<String, BeanField> nodes) {
        List<BeanField> missingMatches = validateMappingRequirements(nodes);
        if (missingMatches.size() > 0) {
            throw new BeanStrictMappingRequirementsException(
                    new BeanMatchValidationMessage(beanPair, missingMatches));
        }
    }

    private List<BeanField> validateMappingRequirements(Map<String, BeanField> fields) {
        List<BeanField> missingMatches = new ArrayList<BeanField>();
        for (String fieldName : fields.keySet()) {
            MatchedBeanPairField matchedField = findBeanPairField(fieldName);
            BeanField sourceBeanField = matchedField.getSourceBeanField();
            BeanField targetBeanField = matchedField.getTargetBeanField();
            if (    sourceBeanField == null || targetBeanField == null) {
                missingMatches.add(sourceBeanField == null ?
                        targetBeanField :
                        sourceBeanField);
            }
        }
        return missingMatches;
    }

    public void checkForMandatoryUnmatchedNodes() {
        if (verified) {
            return;
        }
        verified = true;
        checkForMandatoryUnmatchedNodes("source", beanPair.getSourceClass(), sourceNode);
        checkForMandatoryUnmatchedNodes("target", beanPair.getTargetClass(), targetNode);
    }

    private void checkForMandatoryUnmatchedNodes(String side, Class<?> containingClass, Map<String, BeanField> nodes) {
        for (String key : nodes.keySet()) {
            BeanField currentField = nodes.get(key);
            if (currentField.isUnmatched()) {
                throw new BeanNoSuchPropertyException(side + " " + containingClass.getCanonicalName() + " has no match for property " + key);
            }
        }
    }

}
