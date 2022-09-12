package io.beanmapper.dynclass;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.Test;

class NodeTest {

    @Test
    void key() {
        Node node = Node.createTree(Arrays.asList("id", "name", "assets.id", "assets.name"));
        assertEquals("assets(id,name),id,name", node.getKey());
    }

    @Test
    void emptyKey() {
        Node node = Node.createTree(Collections.emptyList());
        assertEquals("", node.getKey());
    }

    @Test
    void complexKey() {
        Node node = Node.createTree(
                Arrays.asList("id", "name", "assets.id", "assets.name", "assets.artists",
                        "assets.artists.id", "assets.artists.name"));
        assertEquals("assets(artists(id,name),id,name),id,name", node.getKey());
    }

}
