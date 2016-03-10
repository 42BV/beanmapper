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

import java.lang.reflect.Field;
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
            return getOrCreateGeneratedClass(targetClass, displayFields);
        } catch (Exception err) {
            throw new BeanDynamicClassGenerationException(
                    err,
                    getConfiguration().getTargetClass(),
                    displayFields.getKey());
        }
    }

    protected GeneratedClass getOrCreateGeneratedClass(Class<?> targetClass, Node displayFields) throws CannotCompileException, NotFoundException {
        String classInPackage = targetClass.getName();
        Map<String, GeneratedClass> generatedClassesForClass = CACHE.get(classInPackage);
        if (generatedClassesForClass == null) {
            generatedClassesForClass = new TreeMap<String, GeneratedClass>();
            CACHE.put(classInPackage, generatedClassesForClass);
        }
        GeneratedClass generatedClass = generatedClassesForClass.get(displayFields.getKey());
        if (generatedClass == null) {
            String newClassName = classInPackage + "Dyn" + ++GENERATED_CLASS_PREFIX;
            CtClass dynamicClass = classPool.makeClass(newClassName);
            processClassTree(dynamicClass, displayFields, targetClass);
            generatedClass = new GeneratedClass(dynamicClass);
            generatedClassesForClass.put(displayFields.getKey(), generatedClass);
        }
        return generatedClass;
    }

    private void processClassTree(CtClass dynClass, Node node, Class<?> targetClass) throws CannotCompileException, NotFoundException {
        for(Field field : targetClass.getDeclaredFields()) {
            if(node.getFields().contains(field.getName())) {
                Node fieldNode = node.getNode(field.getName());
                CtClass fieldType;
                AnnotationsAttribute attr = null;

                if(fieldNode.hasNodes()) {
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        BeanCollection beanCollection = field.getAnnotation(BeanCollection.class);
                        GeneratedClass elementClass = getOrCreateGeneratedClass(beanCollection.elementType(), fieldNode);

                        // Add BeanCollection annotion on new field
                        ConstPool constPool = dynClass.getClassFile().getConstPool();
                        attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
                        Annotation annot = new Annotation(BeanCollection.class.getName(), constPool);
                        annot.addMemberValue("elementType", new ClassMemberValue(elementClass.generatedClass.getName(), constPool));
                        attr.addAnnotation(annot);
                        fieldType = classPool.getCtClass(field.getType().getName());
                    } else {
                        GeneratedClass nestedClass = getOrCreateGeneratedClass(field.getType(), fieldNode);
                        fieldType = nestedClass.ctClass;
                    }
                } else {
                    fieldType = classPool.getCtClass(field.getType().getName());
                }
                CtField generatedField = new CtField(fieldType, field.getName(), dynClass);
                generatedField.getFieldInfo().setAccessFlags(1);
                if(attr != null) {
                    generatedField.getFieldInfo().addAttribute(attr);
                }
                dynClass.addField(generatedField);
            }
        }
    }
}
