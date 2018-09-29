package io.beanmapper.dynclass;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.beanmapper.config.StrictMappingProperties;
import io.beanmapper.core.BeanMatchStoreFactory;
import io.beanmapper.exceptions.BeanDynamicClassGenerationException;

public class ClassStore {

    private static Map<String, Map<String, Class<?>>> CACHE;
    private ClassGenerator classGenerator;

    static {
        CACHE = new TreeMap<String, Map<String, Class<?>>>();
    }

    public ClassStore(BeanMatchStoreFactory beanMatchStoreFactory) {
        this.classGenerator = new ClassGenerator(beanMatchStoreFactory);
    }

    public Class<?> getOrCreateGeneratedClass(
            Class<?> baseClass, List<String> includeFields,
            StrictMappingProperties strictMappingProperties) {
        Node displayNodes = Node.createTree(includeFields);
        String baseClassName = baseClass.getName();
        Map<String, Class<?>> generatedClassesForClass = null;

        synchronized (CACHE) {
            generatedClassesForClass = CACHE.get(baseClassName);
            if (generatedClassesForClass == null) {
                generatedClassesForClass = new TreeMap<String, Class<?>>();
                CACHE.put(baseClassName, generatedClassesForClass);
            }
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
