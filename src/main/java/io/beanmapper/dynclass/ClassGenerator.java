package io.beanmapper.dynclass;

import java.util.Map;
import java.util.Set;

import io.beanmapper.annotations.BeanAlias;
import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.config.StrictMappingProperties;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.BeanProperty;
import io.beanmapper.core.converter.collections.BeanCollectionInstructions;
import io.beanmapper.core.inspector.BeanPropertySelector;
import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassMap;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.bytecode.AccessFlag;
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
        this.beanMatchStore = new BeanMatchStore(null, null, new BeanPropertySelector());
        this.classPool = classPool;
    }

    /**
     * Creates a class, derived from the given base-class.
     *
     * <p>A class is dynamically generated, whenever a source or target has been downsized, and the given combination of base-class and nodes has not been
     * registered before.</p><p>A class will also be generated when an object is mapped to a record.</p>
     *
     * @param baseClass The class that will be used to generate a blueprint for the new class.
     * @param displayNodes A wrapper for the fields that need to be present in the resulting class.
     * @param strictMappingProperties Configuration regarding strict mapping.
     * @return A {@link GeneratedClass}-object representing a {@link CtClass} and a {@link Class}.
     * @throws NotFoundException May be thrown when Javassist creates a CtClass based on the name of the given base-class.
     * @throws CannotCompileException If Javassist cannot compile the CtClass to bytecode, a CannotCompileException will be thrown.
     */
    public GeneratedClass createClass(
            Class<?> baseClass, Node displayNodes,
            StrictMappingProperties strictMappingProperties) throws NotFoundException, CannotCompileException, ClassNotFoundException {
        this.classPool.insertClassPath(new ClassClassPath(baseClass));
        if (baseClass.isRecord()) {
            //noinspection unchecked
            return createClassDerivedFromRecord((Class<? extends Record>) baseClass, displayNodes.getFields());
        }
        Map<String, BeanProperty> baseFields = beanMatchStore.getBeanMatch(
                strictMappingProperties.createBeanPair(baseClass, Object.class)
        ).getSourceNodes();
        return new GeneratedClass(createClass(baseClass, baseFields, displayNodes, strictMappingProperties), baseClass);
    }

    /**
     * Creates a class specifically derived from a Record-class.
     *
     * <p>This method is used whenever a record is used as a source and the configuration allows for the use of converters.</p>
     *
     * @param clazz The class that will serve as the baseclass for the dynamically generated class.
     * @return A GeneratedClass based on the source class.
     * @throws NotFoundException May be thrown when Javassist creates a CtClass, based on the name of the given base-class.
     * @throws CannotCompileException If Javassist cannot compile the CtClass to bytecode, a CannotCompileException is thrown.
     */
    public synchronized GeneratedClass createClassDerivedFromRecord(Class<? extends Record> clazz, Set<String> fieldNames)
            throws NotFoundException, CannotCompileException {
        CtClass baseClass = classPool.getCtClass(clazz.getName());
        CtClass intermediaryCtClass = classPool.makeClass(baseClass.getName() + "Dyn" + generatedClassPrefix++);

        for (var fieldName : fieldNames) {
            CtField copyField;
            copyField = new CtField(baseClass.getField(fieldName), intermediaryCtClass);

            copyField.setModifiers(AccessFlag.PUBLIC);
            intermediaryCtClass.addField(copyField);
        }
        return new GeneratedClass(intermediaryCtClass, clazz);
    }

    private synchronized CtClass createClass(
            Class<?> base, Map<String, BeanProperty> baseFields,
            Node displayNodes, StrictMappingProperties strictMappingProperties) throws NotFoundException, CannotCompileException, ClassNotFoundException {
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
            Node displayNodes, StrictMappingProperties strictMappingProperties) throws NotFoundException, CannotCompileException, ClassNotFoundException {
        GeneratedClass nestedClass = createClass(type, displayNodes, strictMappingProperties);
        field.setType(nestedClass.ctClass);
        return nestedClass;
    }

    private void handleBeanCollection(
            CtField field, BeanCollectionInstructions collectionInstructions,
            Node displayNodes, StrictMappingProperties strictMappingProperties) throws NotFoundException, CannotCompileException, ClassNotFoundException {
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
