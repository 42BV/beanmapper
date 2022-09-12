package io.beanmapper.dynclass;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.beanmapper.dynclass.model.Person;
import io.beanmapper.exceptions.BeanNoNeighboursException;
import javassist.ClassPool;
import javassist.CtClass;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DynamicClassGeneratorHelperTest {

    private ClassPool classPool;

    @BeforeEach
    void initClassPool() {
        this.classPool = ClassPool.getDefault();
    }

    @Test
    void testNoNeighbourFoundDueToNonExistentPackage() {
        assertThrows(BeanNoNeighboursException.class, () -> {
            CtClass ctClass = classPool.makeClass("io.beanmapper.dynclass.no_neighbours.TestClass");
            DynamicClassGeneratorHelper.getNeighbourClass(ctClass);
        });
    }

    @Test
    void testPackageOfNeighbourEqualsPackageOfMappedClass() {
        CtClass personCtClass = classPool.makeClass("io.beanmapper.dynclass.model.Person");
        Class<?> neighbourClass = DynamicClassGeneratorHelper.getNeighbourClass(personCtClass);
        assertEquals(neighbourClass.getPackage(), Person.class.getPackage());
    }
}