package io.beanmapper.dynclass;

import javassist.*;

import java.util.ArrayList;
import java.util.List;

public class ClassBuilder {

    private CtClass baseClass;
    private CtClass dynClass;
    private List<FieldBuilder> fields;
    private List<MethodBuilder> methods;

    public ClassBuilder(CtClass baseClass, CtClass dynClass) {
        this.baseClass = baseClass;
        this.dynClass = dynClass;
        this.fields = new ArrayList<FieldBuilder>();
        this.methods = new ArrayList<MethodBuilder>();
    }

    public FieldBuilder copyField(String fieldName) throws NotFoundException, CannotCompileException {
        FieldBuilder fieldBuilder = FieldBuilder.copy(this, fieldName);
        this.fields.add(fieldBuilder);
        return fieldBuilder;
    }

    public MethodBuilder copyMethod(String methodName) throws NotFoundException, CannotCompileException {
        MethodBuilder methodBuilder = MethodBuilder.copy(this, methodName);
        this.methods.add(methodBuilder);
        return methodBuilder;
    }

    public CtClass build() throws CannotCompileException {
        for(FieldBuilder fieldBuilder : fields) {
            dynClass.addField(fieldBuilder.build());
        }
        for(MethodBuilder methodBuilder : methods) {
            dynClass.addMethod(methodBuilder.build());
        }
        return dynClass;
    }

    public CtClass getBaseClass() {
        return baseClass;
    }

    public CtClass getDynClass() {
        return dynClass;
    }
}
