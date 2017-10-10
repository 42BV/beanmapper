package io.beanmapper.dynclass;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.Test;

public class NodeTest {

    @Test
    public void key() {
        Node node = Node.createTree(Arrays.asList("id", "name", "assets.id", "assets.name"));
        assertEquals("assets(id,name),id,name", node.getKey());
    }

    @Test
    public void emptyKey() {
        Node node = Node.createTree(Collections.<String> emptyList());
        assertEquals("", node.getKey());
    }

    @Test
    public void complexKey() {
        Node node = Node.createTree(
                Arrays.asList("id", "name", "assets.id", "assets.name", "assets.artists",
                        "assets.artists.id", "assets.artists.name"));
        assertEquals("assets(artists(id,name),id,name),id,name", node.getKey());
    }

}
