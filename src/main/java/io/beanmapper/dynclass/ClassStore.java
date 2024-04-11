package io.beanmapper.dynclass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.beanmapper.config.StrictMappingProperties;
import io.beanmapper.exceptions.BeanDynamicClassGenerationException;

public class ClassStore {

    private static final Map<String, Map<String, Class<?>>> CACHE = new ConcurrentHashMap<>();
    private final ClassGenerator classGenerator;

    public ClassStore() {
        this(new ClassGenerator());
    }

    public ClassStore(ClassGenerator classGenerator) {
        this.classGenerator = classGenerator;
    }

    public Class<?> getOrCreateGeneratedClass(
            Class<?> baseClass, List<String> includeFields,
            StrictMappingProperties strictMappingProperties) {
        Node displayNodes = Node.createTree(includeFields);
        String baseClassName = baseClass.getName();
        Map<String, Class<?>> generatedClassesForClass;

        synchronized (CACHE) {
            generatedClassesForClass = CACHE.computeIfAbsent(baseClassName, k -> new HashMap<>());
        }

        synchronized (generatedClassesForClass) {
            Class<?> generatedClass = generatedClassesForClass.get(displayNodes.getKey());
            if (generatedClass == null) {
                try {
                    generatedClass = classGenerator.createClass(
                            baseClass,
                            displayNodes,
                            strictMappingProperties).generatedClass;
                } catch (Exception err) {
                    throw new BeanDynamicClassGenerationException(err, baseClass, displayNodes.getKey());
                }
                generatedClassesForClass.put(displayNodes.getKey(), generatedClass);
            }
            return generatedClass;
        }
    }
}
