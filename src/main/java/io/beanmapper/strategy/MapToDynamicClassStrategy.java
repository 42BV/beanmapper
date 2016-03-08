package io.beanmapper.strategy;

import io.beanmapper.BeanMapper;
import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.config.Configuration;
import io.beanmapper.dynclass.GeneratedClass;
import io.beanmapper.dynclass.Node;
import io.beanmapper.exceptions.BeanDynamicClassGenerationException;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;

import java.util.Collection;
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
        List<String> includeFields = getConfiguration().getIncludeFields();
        if (includeFields == null || includeFields.size() == 0) {
            // Force re-entry through the core map method, but disregard the include fields now
            return getBeanMapper()
                    .config()
                        .setIncludeFields(null)
                        .build()
                    .map(source);
        }

        // generate or reuse a dynamic class
        final Class dynamicClass = getOrCreateGeneratedClass(getConfiguration().determineTargetClass(), includeFields).generatedClass;

        // If no collection class is set, but we are dealing with a collection class, make sure it is set
        Class collectionClass = getConfiguration().getCollectionClass();
        if (collectionClass == null && Collection.class.isAssignableFrom(source.getClass())) {
            collectionClass = source.getClass();
        }

        Object result = getBeanMapper()
                .config()
                .setCollectionClass(collectionClass)
                .setIncludeFields(null)
                .setTargetClass(dynamicClass)
                .build()
            .map(source);

        Object target = getConfiguration().getTarget();
        if (target != null) {
            result = getBeanMapper()
                    .wrapConfig()
                    .setTarget(target)
                    .build()
                .map(result);
        }

        return result;
    }

    private GeneratedClass getOrCreateGeneratedClass(Class<?> targetClass, List<String> includeFields) {
        Node displayFields = Node.createTree(includeFields);
        try {
            return getOrCreateGeneratedClass(targetClass.getName(), displayFields);
        } catch (Exception err) {
            throw new BeanDynamicClassGenerationException(
                    err,
                    getConfiguration().getTargetClass(),
                    displayFields.getKey());
        }
    }

    protected GeneratedClass getOrCreateGeneratedClass(String classInPackage, Node displayFields) throws NotFoundException, CannotCompileException, ClassNotFoundException {
        Map<String, GeneratedClass> generatedClassesForClass = CACHE.get(classInPackage);
        if (generatedClassesForClass == null) {
            generatedClassesForClass = new TreeMap<String, GeneratedClass>();
            CACHE.put(classInPackage, generatedClassesForClass);
        }
        GeneratedClass generatedClass = generatedClassesForClass.get(displayFields.getKey());
        if (generatedClass == null) {
            CtClass dynamicClass = classPool.get(classInPackage);
            dynamicClass.setName(classInPackage + "Dyn" + ++GENERATED_CLASS_PREFIX);
            processClassTree(dynamicClass, displayFields);
            generatedClass = new GeneratedClass(dynamicClass);
            generatedClassesForClass.put(displayFields.getKey(), generatedClass);
        }
        return generatedClass;
    }

    private void processClassTree(CtClass dynClass, Node node) throws NotFoundException, CannotCompileException, ClassNotFoundException {
        for (CtField field : dynClass.getDeclaredFields()) {
            if (node.getFields().contains(field.getName())) {
                Node fieldNode = node.getNode(field.getName());
                // Apply include filter, aka generate new dynamic class
                if (fieldNode.hasNodes() && isMappable(field.getType().getPackageName())) {
                    GeneratedClass nestedClass = getOrCreateGeneratedClass(field.getType().getName(), fieldNode);
                    field.setType(nestedClass.ctClass);
                } else if (field.hasAnnotation(BeanCollection.class)){
                    BeanCollection beanCollection = (BeanCollection) field.getAnnotation(BeanCollection.class);
                    Class<?> elementType = beanCollection.elementType();
                    GeneratedClass elementClass = getOrCreateGeneratedClass(elementType.getName(), fieldNode);

                    elementClass.ctClass.defrost();
                    ConstPool constPool = elementClass.ctClass.getClassFile().getConstPool();
                    AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                    Annotation annot = new Annotation(BeanCollection.class.getName(), constPool);
                    annot.addMemberValue("elementType", new ClassMemberValue(elementClass.generatedClass.getName(), constPool));
                    attr.addAnnotation(annot);
                    field.getFieldInfo().addAttribute(attr);
                    elementClass.ctClass.freeze();
                }
            } else {
                if (node.hasNodes()) {
                    // Only remove fields if there are any fields at all to remove, else assume full showing
                    dynClass.removeField(field);
                }
            }
        }
    }


}
