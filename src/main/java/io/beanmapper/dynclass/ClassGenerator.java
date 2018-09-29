package io.beanmapper.dynclass;

import java.util.Map;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.config.StrictMappingProperties;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.BeanMatchStoreFactory;
import io.beanmapper.core.BeanProperty;
import io.beanmapper.core.converter.collections.BeanCollectionInstructions;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassMap;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;

public class ClassGenerator {

    private final ClassPool classPool;
    private static Integer GENERATED_CLASS_PREFIX = 0;
    private BeanMatchStore beanMatchStore;

    public ClassGenerator(BeanMatchStoreFactory beanMatchStoreFactory) {
        this.beanMatchStore = beanMatchStoreFactory.create(null, null);
        this.classPool = ClassPool.getDefault();
    }

    public GeneratedClass createClass(
            Class<?> baseClass, Node displayNodes,
            StrictMappingProperties strictMappingProperties) throws Exception {
        this.classPool.insertClassPath(new ClassClassPath(baseClass));
        Map<String, BeanProperty> baseFields = beanMatchStore.getBeanMatch(
                strictMappingProperties.createBeanPair(baseClass, Object.class)
        ).getSourceNodes();
        return new GeneratedClass(createClass(baseClass, baseFields, displayNodes, strictMappingProperties));
    }

    private synchronized CtClass createClass(
            Class<?> base, Map<String, BeanProperty> baseFields,
            Node displayNodes, StrictMappingProperties strictMappingProperties) throws Exception {
        CtClass baseClass = classPool.getCtClass(base.getName());
        CtClass dynClass = classPool.makeClass(base.getName() + "Dyn" + ++GENERATED_CLASS_PREFIX);

        for(String key : baseFields.keySet()) {
            if (displayNodes.getFields().contains(key)) {
                BeanProperty beanProperty = baseFields.get(key);
                CtField baseField = baseClass.getField(beanProperty.getName());

                // Field must be included -> copy field with related methods
                CtField generatedField = new CtField(baseField, dynClass);
                dynClass.addField(generatedField);

                CtMethod readMethod = null;
                CtMethod writeMethod = null;
                if (beanProperty.getAccessor().getReadMethod() != null) {
                    CtMethod baseReadMethod = getMethod(baseClass, beanProperty.getAccessor().getReadMethod().getName());
                    readMethod = new CtMethod(baseReadMethod, dynClass, null);
                }
                if (beanProperty.getAccessor().getWriteMethod() != null) {
                    CtMethod baseWriteMethod = getMethod(baseClass, beanProperty.getAccessor().getWriteMethod().getName());
                    writeMethod = new CtMethod(baseWriteMethod, dynClass, null);
                }

                if(displayNodes.getNode(key).hasNodes()) {
                    if(beanProperty.getCollectionInstructions() != null) {
                        handleBeanCollection(generatedField, beanProperty.getCollectionInstructions(), displayNodes.getNode(key), strictMappingProperties);
                    } else {
                        GeneratedClass nestedClass = handleNestedClass(generatedField, beanProperty.getAccessor().getType(), displayNodes.getNode(key),
                                strictMappingProperties);
                        if(readMethod != null) readMethod = changeReadMethod(readMethod, nestedClass.ctClass);
                        if(writeMethod != null) writeMethod = changeWriteMethod(writeMethod, baseField.getType(), nestedClass.ctClass);
                    }
                }

                if(readMethod != null) dynClass.addMethod(readMethod);
                if(writeMethod != null) dynClass.addMethod(writeMethod);
            }
        }
        return dynClass;
    }

    private GeneratedClass handleNestedClass(
            CtField field, Class<?> type,
            Node displayNodes, StrictMappingProperties strictMappingProperties) throws Exception {
        GeneratedClass nestedClass = createClass(type, displayNodes, strictMappingProperties);
        field.setType(nestedClass.ctClass);
        return nestedClass;
    }

    private void handleBeanCollection(
            CtField field, BeanCollectionInstructions collectionInstructions,
            Node displayNodes, StrictMappingProperties strictMappingProperties) throws Exception {
        GeneratedClass elementClass = createClass(collectionInstructions.getCollectionElementType().getType(), displayNodes, strictMappingProperties);

        ConstPool constPool = field.getDeclaringClass().getClassFile().getConstPool();
        AnnotationsAttribute attr= new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation(BeanCollection.class.getName(), constPool);
        annotation.addMemberValue("elementType", new ClassMemberValue(elementClass.generatedClass.getName(), constPool));
        attr.addAnnotation(annotation);
        field.getFieldInfo().addAttribute(attr);
    }

    private CtMethod changeReadMethod(CtMethod readMethod, CtClass newType) throws NotFoundException, CannotCompileException {
        ClassMap classMap = new ClassMap();
        classMap.put(readMethod.getReturnType(), newType);
        return new CtMethod(readMethod, readMethod.getDeclaringClass(), classMap);
    }

    private CtMethod changeWriteMethod(CtMethod writeMethod, CtClass oldType, CtClass newType) throws NotFoundException, CannotCompileException {
        ClassMap classMap = new ClassMap();
        classMap.put(oldType, newType);
        return new CtMethod(writeMethod, writeMethod.getDeclaringClass(), classMap);
    }

    private CtMethod getMethod(CtClass clazz, String methodName) {
        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (NotFoundException e) {
            try {
                return getMethod(clazz.getSuperclass(), methodName);
            } catch (NotFoundException e1) {
                return null;
            }
        }
    }
}
