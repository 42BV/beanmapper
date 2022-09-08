package io.beanmapper.dynclass;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import io.beanmapper.exceptions.BeanNoNeighboursException;
import javassist.CtClass;

public class DynamicClassGeneratorHelper {

    private DynamicClassGeneratorHelper() {
    }

    /**
     * Gets a neighbouring {@link Class class} relative to the class represented by the given {@link CtClass}.
     * This is done so that it can be passed to the {@link CtClass#toClass(Class)}-method,
     * which should be used for compatibility with Java 16+ Reflection.
     * @param ctClass - representation of a class.
     * @return A neighbouring class relative to the class represented by the CtClass.
     */
    public static Class<?> getNeighbourClass(CtClass ctClass) {
        try (InputStream inputStream = ClassLoader.getSystemResourceAsStream(ctClass.getPackageName().replaceAll("[.]", "/"));
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
             BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            String className = bufferedReader.lines()
                    .filter(line -> line.endsWith(".class"))
                    .findFirst()
                    .orElseThrow(ClassNotFoundException::new);
            return Class.forName(ctClass.getPackageName() + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (IOException | ClassNotFoundException | NullPointerException e) {
            throw new BeanNoNeighboursException();
        }
    }
}
