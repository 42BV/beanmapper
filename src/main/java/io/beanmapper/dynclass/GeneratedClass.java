/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.dynclass;

import javassist.CannotCompileException;
import javassist.CtClass;

public class GeneratedClass {

    public final CtClass ctClass;

    public final Class<?> generatedClass;

    public GeneratedClass(CtClass ctClass, Class<?> baseClass) throws CannotCompileException {
        this.ctClass = ctClass;
        this.generatedClass = ctClass.toClass(baseClass);
    }

}