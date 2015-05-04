package nl.mad.beanmapper.core;

public class Route {

    private String[] route = new String[0];

    public Route(String path) {
        if (path == null) {
            return;
        }
        this.route = path.split("\\.");
    }

    public String[] getRoute() {
        return this.route;
    }

}
