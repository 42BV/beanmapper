package io.beanmapper.dynclass;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class MethodBuilder {

    private CtMethod ctMethod;

    private MethodBuilder(CtClass baseClass, CtClass dynClass, String name) throws NotFoundException, CannotCompileException {
        this.ctMethod = new CtMethod(baseClass.getDeclaredMethod(name), dynClass, null);
    }

    public static MethodBuilder copy(ClassBuilder classBuilder, String name) throws NotFoundException, CannotCompileException {
        return new MethodBuilder(classBuilder.getBaseClass(), classBuilder.getDynClass(), name);
    }

    public MethodBuilder returnType(CtClass returnType) throws NotFoundException, CannotCompileException {
        //new CtMethod(returnType, ctMethod.getName(), ctMethod.getParameterTypes(), ctMethod.getDeclaringClass());
        return this;
    }

    public MethodBuilder changeParam(CtClass oldType, CtClass newType) throws NotFoundException, CannotCompileException {
//        CtClass[] parameters = ctMethod.getParameterTypes();
//        for(int i=0; i<parameters.length; i++) {
//            if(parameters[i] == oldType) {
//                parameters[i] = newType;
//                break;
//            }
//        }
//        new CtMethod(ctMethod.getReturnType(), ctMethod.getName(), parameters, ctMethod.getDeclaringClass());
        return this;
    }

    public CtMethod build() {
        return ctMethod;
    }
}
