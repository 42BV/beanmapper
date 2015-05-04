package nl.mad.beanmapper.core;

import nl.mad.beanmapper.annotations.BeanIgnore;
import nl.mad.beanmapper.annotations.BeanProperty;
import nl.mad.beanmapper.annotations.BeanUnwrap;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.TreeMap;

public class BeanMatchStore {

    private Map<String, Map<String, BeanMatch>> store = new TreeMap<>();

    public BeanMatch getBeanMatch(Class source, Class target) throws Exception {
        Map<String, BeanMatch> targetsForSource = getTargetsForSource(source);
        if (targetsForSource == null) {
            return addBeanMatch(determineBeanMatch(source, target));
        }
        return getTarget(targetsForSource, target);
    }

    public BeanMatch addBeanMatch(BeanMatch beanMatch) {
        Map<String, BeanMatch> targetsForSource = getTargetsForSource(beanMatch.getSource());
        if (targetsForSource == null) {
            targetsForSource = new TreeMap<>();
            storeTargetsForSource(beanMatch.getSource(), targetsForSource);
        }
        storeTarget(targetsForSource, beanMatch.getTarget(), beanMatch);
        return beanMatch;
    }

    private void storeTargetsForSource(Class source, Map<String, BeanMatch> targetsForSource) {
        store.put(source.getCanonicalName(), targetsForSource);
    }

    private Map<String, BeanMatch> getTargetsForSource(Class source) {
        return store.get(source.getCanonicalName());
    }

    private BeanMatch getTarget(Map<String, BeanMatch> targetsForSource, Class target) {
        return targetsForSource.get(target.getCanonicalName());
    }

    private void storeTarget(Map<String, BeanMatch> targetsForSource, Class target, BeanMatch beanMatch) {
        targetsForSource.put(target.getCanonicalName(), beanMatch);
    }

    private BeanMatch determineBeanMatch(Class source, Class target) throws Exception {
        return determineBeanMatch(source, target, new TreeMap<>(), new TreeMap<>());
    }

    private BeanMatch determineBeanMatch(Class source, Class target,
                                         Map<String, BeanField> sourceNode, Map<String, BeanField> targetNode) throws Exception {
        return new BeanMatch(
                source,
                target,
                getAllFields(sourceNode, targetNode, source, target, null),
                getAllFields(targetNode, sourceNode, target, source, null));
    }

    private Map<String, BeanField> getAllFields(Map<String, BeanField> ourNodes, Map<String, BeanField> otherNodes,
                                           Class ourType, Class otherType, BeanField prefixingBeanField) throws Exception {
        // Get field from super class
        if (ourType.getSuperclass() != null) {
            getAllFields(ourNodes, otherNodes, ourType.getSuperclass(), otherType, null);
        }

        for (Field field : ourType.getDeclaredFields()) {

            // Ignore fields
            if (field.isAnnotationPresent(BeanIgnore.class)) {
                continue;
            }

            // BeanProperty allows the field to match with a field from the other side with a different name
            // and even a different nesting level.
            String name = dealWithBeanProperty(otherNodes, otherType, field);

            // Unwrap the fields which exist in the unwrap class
            BeanField currentBeanField = BeanField.determineNodesForPath(ourType, field.getName(), prefixingBeanField);
            if (field.isAnnotationPresent(BeanUnwrap.class)) {
                ourNodes = getAllFields(ourNodes, otherNodes, field.getType(), otherType, currentBeanField);
            } else {
                ourNodes.put(name, currentBeanField);
            }
        }
        return ourNodes;
    }

    private String dealWithBeanProperty(Map<String, BeanField> otherNodes, Class otherType, Field field) {
        String name = field.getName();
        if (field.isAnnotationPresent(BeanProperty.class)) {
            name = field.getAnnotation(BeanProperty.class).name();
            // Get the other field from the location that is specified in the beanProperty annotation.
            // If the field is referred to by a path, store the custom field in the other map
            try {
                otherNodes.put(name, BeanField.determineNodesForPath(otherType, name, null));
            } catch (NoSuchFieldException err) {
                // Acceptable, might have been tagged as @BeanProperty as well
            }
        }
        return name;
    }

}
