package io.beanmapper.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import io.beanmapper.testmodel.nestedclasses.Layer1;
import io.beanmapper.testmodel.nestedclasses.Layer1Result;
import io.beanmapper.testmodel.tostring.SourceWithNonString;
import io.beanmapper.testmodel.tostring.TargetWithString;

import java.time.LocalDate;

import org.junit.Test;

public class BeanFieldTest {

    @Test
    public void determineNodes() throws Exception {
        BeanField beanField = BeanField.determineNodesForPath(Layer1.class, "layer2.layer3.name3");
        assertEquals("layer2", beanField.getCurrentField().getName());
        assertNotNull("the 'layer2' node must refer to the 'layer3' node", beanField.getNext());
        assertEquals("layer3", beanField.getNext().getCurrentField().getName());
        assertNotNull("the 'layer3' node must refer to the 'name3' node", beanField.getNext().getNext());
        assertEquals("name3", beanField.getNext().getNext().getCurrentField().getName());
    }

    @Test
    public void getObject() throws Exception {
        Layer1 layer1 = Layer1.createNestedClassObject();
        BeanField beanField = BeanField.determineNodesForPath(Layer1.class, "layer2.layer3.name3");
        Object object = beanField.getObject(layer1);
        assertEquals(String.class, object.getClass());
        assertEquals("name3", object);
    }

    @Test
    public void writeObjectToNewTarget() throws Exception {
        Layer1 layer1 = Layer1.createNestedClassObject();
        BeanField beanFieldForSource = BeanField.determineNodesForPath(Layer1.class, "layer2.layer3.name3");
        Object source = beanFieldForSource.getObject(layer1);
        BeanField beanFieldForTarget = BeanField.determineNodesForPath(Layer1Result.class, "layer2.layer3.name3");
        Layer1Result target = (Layer1Result) beanFieldForTarget.writeObject(source, new Layer1Result());
        assertEquals("name3", target.getLayer2().getLayer3().getName3());
    }

    @Test
    public void writeObjectToExistingTarget() throws Exception {
        Layer1 layer1 = Layer1.createNestedClassObject();
        Layer1Result existingTarget = Layer1Result.createNestedClassObject();
        BeanField beanFieldForSource = BeanField.determineNodesForPath(Layer1.class, "layer2.layer3.name3");
        Object source = beanFieldForSource.getObject(layer1);
        BeanField beanFieldForTarget = BeanField.determineNodesForPath(Layer1Result.class, "layer2.layer3.name3");
        Layer1Result target = (Layer1Result) beanFieldForTarget.writeObject(source, existingTarget);
        assertEquals("name3", target.getLayer2().getLayer3().getName3());
    }

    @Test
    public void nonStringToString() throws Exception {
        SourceWithNonString source = new SourceWithNonString();
        source.setDate(LocalDate.of(2015, 4, 1));
        BeanField beanFieldForSource = BeanField.determineNodesForPath(SourceWithNonString.class, "date");
        BeanField beanFieldForTarget = BeanField.determineNodesForPath(TargetWithString.class, "date");
        TargetWithString target = (TargetWithString)
                beanFieldForTarget.writeObject(beanFieldForSource.getObject(source), new TargetWithString());
        assertEquals("2015-04-01", target.getDate());
    }

    @Test
    public void getName() throws Exception {
        BeanField beanFieldForSource = BeanField.determineNodesForPath(Layer1.class, "layer2.layer3.name3");
        assertEquals("layer2.layer3.name3", beanFieldForSource.getName());
    }

}
