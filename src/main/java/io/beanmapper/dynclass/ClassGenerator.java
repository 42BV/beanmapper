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
                dynClass.addField(generatedField);

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
                        handleBeanCollection(dynClass, generatedField, baseField.getCollectionInstructions(), displayNodes.getNode(key));
                    } else {
                        GeneratedClass nestedClass = handleNestedClass(generatedField, baseField.getProperty().getType(), displayNodes.getNode(key));
                        if(readMethod != null) readMethod = changeReadMethod(readMethod, nestedClass.ctClass);
                        if(writeMethod != null) writeMethod = changeWriteMethod(writeMethod, baseField.getProperty().getType(), nestedClass.ctClass);
                    }
                }

                if(readMethod != null) dynClass.addMethod(readMethod);
                if(writeMethod != null) dynClass.addMethod(writeMethod);
            }
        }
        return dynClass;
    }

    private GeneratedClass handleNestedClass(CtField field, Class<?> type, Node displayNodes) throws Exception {
        GeneratedClass nestedClass = createClass(type, displayNodes);
        field.setType(nestedClass.ctClass);
        return nestedClass;
    }

    private void handleBeanCollection(CtClass dynClass, CtField field, BeanCollectionInstructions collectionInstructions, Node displayNodes) throws Exception {
        GeneratedClass elementClass = createClass(collectionInstructions.getCollectionMapsTo(), displayNodes);

        ConstPool constPool = dynClass.getClassFile().getConstPool();
        AnnotationsAttribute attr= new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annotation = new Annotation(BeanCollection.class.getName(), constPool);
        annotation.addMemberValue("elementType", new ClassMemberValue(elementClass.generatedClass.getName(), constPool));
        attr.addAnnotation(annotation);
        field.getFieldInfo().addAttribute(attr);
    }

    private CtMethod changeReadMethod(CtMethod readMethod, CtClass newType) throws NotFoundException, CannotCompileException {
        CtMethod newReadMethod = new CtMethod(newType, readMethod.getName(), readMethod.getParameterTypes(), readMethod.getDeclaringClass());
        newReadMethod.setBody(readMethod, null);
        return null;
    }

    private CtMethod changeWriteMethod(CtMethod writeMethod, Class<?> oldType, CtClass newType) throws NotFoundException, CannotCompileException {
        CtClass ctType = classPool.getCtClass(oldType.getName());
        CtClass[] parameters = writeMethod.getParameterTypes();
        for(int i=0; i<parameters.length; i++) {
            if(parameters[i] == ctType) parameters[i] = newType;
        }
        CtMethod newWriteMethod = new CtMethod(writeMethod.getReturnType(), writeMethod.getName(), parameters, writeMethod.getDeclaringClass());
        newWriteMethod.setBody(writeMethod, null);
        return null;
    }
}
