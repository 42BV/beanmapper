package io.beanmapper.dynclass;

import io.beanmapper.core.Route;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class Node {

    private Map<String, Node> nodes = new TreeMap<String, Node>();

    public Node getNode(String name) {
        return nodes.get(name);
    }

    public Node addNode(String name) {
        Node newNode = new Node();
        this.nodes.put(name, newNode);
        return newNode;
    }

    public Set<String> getFields() {
        return nodes.keySet();
    }

    public boolean hasNodes() {
        return nodes.size() > 0;
    }

    public static Node createTree(List<String> fields) {
        Node root = new Node();
        for (String field : fields) {
            Route route = new Route(field);
            Node current = root;
            for (String routePart : route.getRoute()) {
                Node newCurrent = current.getNode(routePart);
                if (newCurrent == null) {
                    newCurrent = current.addNode(routePart);
                }
                current = newCurrent;
            }
        }
        return root;
    }

    public String getKey() {
        StringBuffer key = new StringBuffer();
        boolean first = true;
        for (String field : nodes.keySet()) {
            if (!first) {
                key.append(',');
            }
            key.append(field);
            Node fieldNode = nodes.get(field);
            if (fieldNode.hasNodes()) {
                key.append("(" + fieldNode.getKey() + ")");
            }
            first = false;
        }
        return key.toString();
    }

}
