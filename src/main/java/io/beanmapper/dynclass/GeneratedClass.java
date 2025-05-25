/*
 * (C) 2014 42 bv (www.42.nl). All rights reserved.
 */
package io.beanmapper.dynclass;

import javassist.CannotCompileException;
import javassist.CtClass;

public record GeneratedClass(CtClass ctClass, Class<?> generatedClass) {

    public static GeneratedClass create(CtClass ctClass, Class<?> baseClass) throws CannotCompileException {
        return new GeneratedClass(ctClass, ctClass.toClass(baseClass));
    }

}