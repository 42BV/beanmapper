package io.beanmapper.dynclass;

import static org.junit.Assert.assertEquals;

import io.beanmapper.dynclass.model.Person;
import io.beanmapper.exceptions.BeanNoNeighboursException;
import javassist.ClassPool;
import javassist.CtClass;

import org.junit.Before;
import org.junit.Test;

public class DynamicClassGeneratorHelperTest {

    private ClassPool classPool;

    @Before
    public void initClassPool() {
        this.classPool = ClassPool.getDefault();
    }

    @Test(expected = BeanNoNeighboursException.class)
    public void testNoNeighbourFoundDueToNonExistentPackage() {
        CtClass ctClass = classPool.makeClass("io.beanmapper.dynclass.no_neighbours.TestClass");
        DynamicClassGeneratorHelper.getNeighbourClass(ctClass);
    }

    @Test
    public void testPackageOfNeighbourEqualsPackageOfMappedClass() {
        CtClass personCtClass = classPool.makeClass("io.beanmapper.dynclass.model.Person");
        Class<?> neighbourClass = DynamicClassGeneratorHelper.getNeighbourClass(personCtClass);
        assertEquals(neighbourClass.getPackage(), Person.class.getPackage());
    }
}