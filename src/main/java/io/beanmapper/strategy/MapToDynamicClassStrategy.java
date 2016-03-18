package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanCollectionUsage;
import io.beanmapper.config.Configuration;
import io.beanmapper.core.BeanField;
import io.beanmapper.dynclass.ClassBuilder;
import io.beanmapper.dynclass.GeneratedClass;
import io.beanmapper.dynclass.Node;
import io.beanmapper.exceptions.BeanDynamicClassGenerationException;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MapToDynamicClassStrategy extends AbstractMapStrategy {

    private static Map<String, Map<String, GeneratedClass>> CACHE;
    private static Integer GENERATED_CLASS_PREFIX = 0;

    private final ClassPool classPool;

    static {
        CACHE = new TreeMap<String, Map<String, GeneratedClass>>();
    }

    public MapToDynamicClassStrategy(BeanMapper beanMapper, Configuration configuration) {
        super(beanMapper, configuration);
        this.classPool = ClassPool.getDefault();
        this.classPool.insertClassPath(new ClassClassPath(configuration.determineTargetClass()));
    }

    @Override
    public Object map(Object source) {
        List<String> limitSourceFields = getConfiguration().getDownsizeSource();
        List<String> limitTargetFields = getConfiguration().getDownsizeTarget();
        if (limitSourceFields != null && limitSourceFields.size() > 0) {
            return limitSource(source, limitSourceFields);
        } else if (limitTargetFields != null && limitTargetFields.size() > 0) {
            return limitTarget(source, limitTargetFields);
        } else {
            // Force re-entry through the core map method, but disregard the include fields now
            return getBeanMapper()
                    .config()
                    .limitSource(null)
                    .limitTarget(null)
                    .build()
                    .map(source);
        }
    }

    public Object limitSource(Object source, List<String> limitSourceFields) {
        final Class dynamicClass = getOrCreateGeneratedClass(source.getClass(), limitSourceFields).generatedClass;
        Class<?> targetClass = getConfiguration().getTargetClass();
        Object target = getConfiguration().getTarget();
        Object dynSource = getBeanMapper()
                .config()
                .limitSource(null)
                .setTargetClass(dynamicClass)
                .build()
                .map(source);


        return getBeanMapper()
                .wrapConfig()
                .setTargetClass(targetClass)
                .setTarget(target)
                .build()
                .map(dynSource);
    }

    public Object limitTarget(Object source, List<String> limitTargetFields) {
        final Class dynamicClass = getOrCreateGeneratedClass(getConfiguration().determineTargetClass(), limitTargetFields).generatedClass;
        return getBeanMapper()
                .config()
                .limitTarget(null)
                .setTargetClass(dynamicClass)
                .build()
                .map(source);
    }

    private GeneratedClass getOrCreateGeneratedClass(Class<?> baseClass, List<String> includeFields) {
        Node displayNodes = Node.createTree(includeFields);
        String baseClassName = baseClass.getName();

        // get class from cache
        Map<String, GeneratedClass> generatedClassesForClass = CACHE.get(baseClassName);
        if (generatedClassesForClass == null) {
            generatedClassesForClass = new TreeMap<String, GeneratedClass>();
            CACHE.put(baseClassName, generatedClassesForClass);
        }
        GeneratedClass generatedClass = generatedClassesForClass.get(displayNodes.getKey());
        if (generatedClass == null) {
            try {
                generatedClass = new GeneratedClass(createClass(baseClass, displayNodes));
            } catch (Exception err) {
                throw new BeanDynamicClassGenerationException(err, baseClass, displayNodes.getKey());
            }
            generatedClassesForClass.put(displayNodes.getKey(), generatedClass);
        }
        return generatedClass;
    }

    private CtClass createClass(Class<?> baseClass, Node displayNodes) throws Exception {
        Map<String, BeanField> baseFields = getConfiguration().getBeanMatchStore().getBeanMatch(baseClass, Object.class).getSourceNode();
        return createClass(baseClass, baseFields, displayNodes);
    }

    private CtClass createClass(Class<?> base, Map<String, BeanField> baseFields, Node displayNodes) throws Exception {
        String newClassName = base.getName() + "Dyn" + ++GENERATED_CLASS_PREFIX;
        ClassBuilder classBuilder = new ClassBuilder(classPool.getCtClass(base.getName()), classPool.makeClass(newClassName));

        for(String key : baseFields.keySet()) {
            if (displayNodes.getFields().contains(key)) {
                BeanField baseField = baseFields.get(key);
                if(displayNodes.getNode(key).hasNodes()) {
                    if(baseField.getCollectionInstructions() != null) {
                        Class<?> elementType = baseField.getCollectionInstructions().getCollectionMapsTo();
                        BeanCollectionUsage beanCollectionUsage = baseField.getCollectionInstructions().getBeanCollectionUsage();
                        CtClass elementClass = createClass(elementType, displayNodes.getNode(key));
                        classBuilder.copyField(baseField.getName()).addBeanCollection(elementClass, beanCollectionUsage);
                    } else {
                        CtClass nestedClass = createClass(baseField.getProperty().getType(), displayNodes.getNode(key));
                        classBuilder.copyField(baseField.getName()).type(nestedClass);
                        if (baseField.getProperty().getReadMethod() != null) {
                            classBuilder.copyMethod(baseField.getProperty().getReadMethod().getName()).returnType(nestedClass);
                        }
                        if (baseField.getProperty().getWriteMethod() != null) {
                            CtClass type = classPool.getCtClass(baseField.getProperty().getType().getName());
                            classBuilder.copyMethod(baseField.getProperty().getWriteMethod().getName()).changeParam(type, nestedClass);
                        }
                    }
                } else {
                    classBuilder.copyField(baseField.getName());
                    if (baseField.getProperty().getReadMethod() != null) {
                        classBuilder.copyMethod(baseField.getProperty().getReadMethod().getName());
                    }
                    if (baseField.getProperty().getWriteMethod() != null) {
                        classBuilder.copyMethod(baseField.getProperty().getWriteMethod().getName());
                    }
                }
            }
        }
        return classBuilder.build();
    }
}
