package io.beanmapper.dynclass;

import java.util.Map;

import io.beanmapper.annotations.BeanAlias;
import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.config.StrictMappingProperties;
import io.beanmapper.core.BeanMatchStore;
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

    private static Integer generatedClassPrefix = 0;
    private final ClassPool classPool;
    private final BeanMatchStore beanMatchStore;

    public ClassGenerator() {
        this(ClassPool.getDefault());
    }

    public ClassGenerator(ClassPool classPool) {
        this.beanMatchStore = new BeanMatchStore(null, null);
        this.classPool = classPool;
    }

    public GeneratedClass createClass(
            Class<?> baseClass, Node displayNodes,
            StrictMappingProperties strictMappingProperties) throws Exception {
        this.classPool.insertClassPath(new ClassClassPath(baseClass));
        Map<String, BeanProperty> baseFields = beanMatchStore.getBeanMatch(
                strictMappingProperties.createBeanPair(baseClass, Object.class)
        ).getSourceNodes();
        return new GeneratedClass(createClass(baseClass, baseFields, displayNodes, strictMappingProperties), baseClass);
    }

    private synchronized CtClass createClass(
            Class<?> base, Map<String, BeanProperty> baseFields,
            Node displayNodes, StrictMappingProperties strictMappingProperties) throws Exception {
        CtClass baseClass = classPool.getCtClass(base.getName());
        CtClass dynClass = classPool.makeClass(base.getName() + "Dyn" + ++generatedClassPrefix);

        for (Map.Entry<String, BeanProperty> entry : baseFields.entrySet()) {
            if (displayNodes.getFields().contains(entry.getKey())) {
                BeanProperty beanProperty = entry.getValue();
                CtField baseField = baseClass.getField(beanProperty.getName());

                // Field must be included -> copy field with related methods
                CtField generatedField = new CtField(baseField, dynClass);
                // If the basefield is annotated with BeanAlias, we set the name of the generated field to the value set
                // on the annotation, and remove the annotation from the generated field.
                if (baseField.hasAnnotation(BeanAlias.class)) {
                    generatedField.setName(((BeanAlias) baseField.getAnnotation(BeanAlias.class)).value());
                    ((AnnotationsAttribute) generatedField.getFieldInfo().getAttribute(AnnotationsAttribute.visibleTag))
                            .removeAnnotation(BeanAlias.class.getName());
                }
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

                if (displayNodes.getNode(entry.getKey()).hasNodes()) {
                    if (beanProperty.getCollectionInstructions() != null) {
                        handleBeanCollection(generatedField, beanProperty.getCollectionInstructions(), displayNodes.getNode(entry.getKey()),
                                strictMappingProperties);
                    } else {
                        GeneratedClass nestedClass = handleNestedClass(generatedField, beanProperty.getAccessor().getType(),
                                displayNodes.getNode(entry.getKey()),
                                strictMappingProperties);
                        if (readMethod != null)
                            readMethod = changeReadMethod(readMethod, nestedClass.ctClass);
                        if (writeMethod != null)
                            writeMethod = changeWriteMethod(writeMethod, baseField.getType(), nestedClass.ctClass);
                    }
                }

                if (readMethod != null)
                    dynClass.addMethod(readMethod);
                if (writeMethod != null)
                    dynClass.addMethod(writeMethod);
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
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
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

    private CtMethod changeWriteMethod(CtMethod writeMethod, CtClass oldType, CtClass newType) throws CannotCompileException {
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
