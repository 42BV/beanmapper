package io.beanmapper.dynclass;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import io.beanmapper.core.Route;

public class Node {

    private final Map<String, Node> nodes = new TreeMap<>();

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
        StringBuilder key = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Node> entry : nodes.entrySet()) {
            if (!first) {
                key.append(',');
            }
            key.append(entry.getKey());
            Node fieldNode = entry.getValue();
            if (fieldNode.hasNodes()) {
                key.append("(").append(fieldNode.getKey()).append(")");
            }
            first = false;
        }
        return key.toString();
    }

}
