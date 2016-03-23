package io.beanmapper.dynclass;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.core.BeanField;
import io.beanmapper.core.BeanMatchStore;
import io.beanmapper.core.converter.collections.BeanCollectionInstructions;
import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;

import java.util.Map;

public class ClassGenerator {

    private final ClassPool classPool;
    private static Integer GENERATED_CLASS_PREFIX = 0;
    private BeanMatchStore beanMatchStore;

    public ClassGenerator() {
        this.beanMatchStore = new BeanMatchStore();
        this.classPool = ClassPool.getDefault();
    }

    public GeneratedClass createClass(Class<?> baseClass, Node displayNodes) throws Exception {
        this.classPool.insertClassPath(new ClassClassPath(baseClass));
        Map<String, BeanField> baseFields = beanMatchStore.getBeanMatch(baseClass, Object.class).getSourceNode();
        return new GeneratedClass(createClass(baseClass, baseFields, displayNodes));
    }

    private CtClass createClass(Class<?> base, Map<String, BeanField> baseFields, Node displayNodes) throws Exception {
        CtClass baseClass = classPool.getCtClass(base.getName());
        CtClass dynClass = classPool.makeClass(base.getName() + "Dyn" + ++GENERATED_CLASS_PREFIX);

        for(String key : baseFields.keySet()) {
            if (displayNodes.getFields().contains(key)) {
                BeanField baseField = baseFields.get(key);

                // Field must be included -> copy field with related methods
                CtField generatedField = new CtField(baseClass.getField(baseField.getName()), dynClass);
                CtMethod readMethod = null;
                CtMethod writeMethod = null;
                if (baseField.getProperty().getReadMethod() != null) {
                    CtMethod baseReadMethod = baseClass.getDeclaredMethod(baseField.getProperty().getReadMethod().getName());
                    readMethod = new CtMethod(baseReadMethod, dynClass, null);
                }
                if (baseField.getProperty().getWriteMethod() != null) {
                    CtMethod baseWriteMethod = baseClass.getDeclaredMethod(baseField.getProperty().getWriteMethod().getName());
                    writeMethod = new CtMethod(baseWriteMethod, dynClass, null);
                }

                if(displayNodes.getNode(key).hasNodes()) {
                    if(baseField.getCollectionInstructions() != null) {
                        handleBeanCollection(baseField.getCollectionInstructions(), displayNodes.getNode(key), generatedField);
                    } else {
                        handleNestedClass(baseField.getProperty().getType(), displayNodes.getNode(key), generatedField, readMethod, writeMethod);
                    }
                }

                dynClass.addField(generatedField);
                if(readMethod != null) dynClass.addMethod(readMethod);
                if(writeMethod != null) dynClass.addMethod(writeMethod);
            }
        }
        return dynClass;
    }

    private void handleNestedClass(Class<?> type, Node displayNodes, CtField field, CtMethod readMethod, CtMethod writeMethod) throws Exception {
        CtClass nestedClass = createClass(type, displayNodes).ctClass;
        field.setType(nestedClass);
//        if (readMethod != null) {
//            readMethod.setBody();
//            readMethod.returnType(nestedClass);
//        }
//        if (writeMethod != null) {
//            CtClass ctType = classPool.getCtClass(type.getName());
//            writeMethod.changeParam(type, nestedClass);
//        }
    }

    private void handleBeanCollection(BeanCollectionInstructions collectionInstructions, Node displayNodes, CtField field) throws Exception {
        GeneratedClass elementClass = createClass(collectionInstructions.getCollectionMapsTo(), displayNodes);

        elementClass.ctClass.defrost();
        ConstPool constPool = elementClass.ctClass.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation(BeanCollection.class.getName(), constPool);
        annotation.addMemberValue("elementType", new ClassMemberValue(elementClass.generatedClass.getName(), constPool));
        attr.addAnnotation(annotation);
        field.getFieldInfo().addAttribute(attr);
        elementClass.ctClass.freeze();
    }
}
