package io.beanmapper.dynclass;

import io.beanmapper.annotations.BeanCollection;
import io.beanmapper.annotations.BeanCollectionUsage;
import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.ConstPool;
import javassist.bytecode.annotation.Annotation;
import javassist.bytecode.annotation.ClassMemberValue;

public class FieldBuilder {

    private CtField ctField;

    private FieldBuilder(CtClass baseClass, CtClass dynClass, String name) throws NotFoundException, CannotCompileException {
        this.ctField = new CtField(baseClass.getField(name), dynClass);
    }

    public static FieldBuilder copy(ClassBuilder classBuilder, String name) throws NotFoundException, CannotCompileException {
        return new FieldBuilder(classBuilder.getBaseClass(), classBuilder.getDynClass(), name);
    }

    public FieldBuilder type(CtClass clazz) {
        ctField.setType(clazz);
        return this;
    }

    public FieldBuilder addBeanCollection(CtClass elementType, BeanCollectionUsage beanCollectionUsage) {
        elementType.defrost();
        ConstPool constPool = elementType.getClassFile().getConstPool();
        AnnotationsAttribute attr = new AnnotationsAttribute(constPool, AnnotationsAttribute.visibleTag);
        Annotation annot = new Annotation(BeanCollection.class.getName(), constPool);
        annot.addMemberValue("elementType", new ClassMemberValue(elementType.getName(), constPool));
        attr.addAnnotation(annot);
        ctField.getFieldInfo().addAttribute(attr);
        elementType.freeze();
        return this;
    }

    public CtField build() {
        return ctField;
    }
}
