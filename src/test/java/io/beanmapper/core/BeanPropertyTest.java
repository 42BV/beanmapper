package io.beanmapper.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import io.beanmapper.testmodel.nested_classes.Layer1;
import io.beanmapper.testmodel.nested_classes.Layer1Result;

import org.junit.jupiter.api.Test;

class BeanPropertyTest {

    @Test
    void determineNodes() {
        BeanProperty beanProperty = new BeanPropertyCreator(BeanPropertyMatchupDirection.SOURCE_TO_TARGET, Layer1.class,
                "layer2.layer3.name3").determineNodesForPath();
        assertEquals("layer2", beanProperty.getCurrentAccessor().getName());
        assertNotNull(beanProperty.getNext(), "the 'layer2' node must refer to the 'layer3' node");
        assertEquals("layer3", beanProperty.getNext().getCurrentAccessor().getName());
        assertNotNull(beanProperty.getNext().getNext(), "the 'layer3' node must refer to the 'name3' node");
        assertEquals("name3", beanProperty.getNext().getNext().getCurrentAccessor().getName());
    }

    @Test
    void getObject() {
        Layer1 layer1 = Layer1.createNestedClassObject();
        BeanProperty beanProperty = new BeanPropertyCreator(BeanPropertyMatchupDirection.SOURCE_TO_TARGET, Layer1.class,
                "layer2.layer3.name3").determineNodesForPath();
        Object object = beanProperty.getObject(layer1);
        assertEquals(String.class, object.getClass());
        assertEquals("name3", object);
    }

    @Test
    void writeObjectToNewTarget() {
        Layer1 layer1 = Layer1.createNestedClassObject();
        BeanProperty beanPropertyForSource = new BeanPropertyCreator(BeanPropertyMatchupDirection.SOURCE_TO_TARGET, Layer1.class,
                "layer2.layer3.name3").determineNodesForPath();
        Object source = beanPropertyForSource.getObject(layer1);
        BeanProperty beanPropertyForTarget = new BeanPropertyCreator(BeanPropertyMatchupDirection.TARGET_TO_SOURCE, Layer1Result.class,
                "layer2.layer3.name3").determineNodesForPath();
        Layer1Result target = (Layer1Result) beanPropertyForTarget.writeObject(source, new Layer1Result(), null, null);
        assertEquals("name3", target.getLayer2().getLayer3().getName3());
    }

    @Test
    void writeObjectToExistingTarget() {
        Layer1 layer1 = Layer1.createNestedClassObject();
        Layer1Result existingTarget = Layer1Result.createNestedClassObject();
        BeanProperty beanPropertyForSource = new BeanPropertyCreator(BeanPropertyMatchupDirection.SOURCE_TO_TARGET, Layer1.class,
                "layer2.layer3.name3").determineNodesForPath();
        Object source = beanPropertyForSource.getObject(layer1);
        BeanProperty beanPropertyForTarget = new BeanPropertyCreator(BeanPropertyMatchupDirection.TARGET_TO_SOURCE, Layer1Result.class,
                "layer2.layer3.name3").determineNodesForPath();
        Layer1Result target = (Layer1Result) beanPropertyForTarget.writeObject(source, existingTarget, null, null);
        assertEquals("name3", target.getLayer2().getLayer3().getName3());
    }

    @Test
    void getName() {
        BeanProperty beanPropertyForSource = new BeanPropertyCreator(BeanPropertyMatchupDirection.SOURCE_TO_TARGET, Layer1.class,
                "layer2.layer3.name3").determineNodesForPath();
        assertEquals("layer2.layer3.name3", beanPropertyForSource.getName());
    }

}
