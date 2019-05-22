import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Node {

    private int posX;
    private int posY;
    private double gValue;
    private int heuristic;
    private double rhsValue;
    private double key;
    private String keyForNodeManager;

    Node() {}

    Node(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
        gValue = DStar.MAX_VALUE;
        heuristic = 0;
        rhsValue = DStar.MAX_VALUE;
        key = 0;
        keyForNodeManager = posX+" "+posY;
    }

    int getPosX() {
        return posX;
    }

    int getPosY() {
        return posY;
    }

    double getgValue() {
        return gValue;
    }

    void setgValue(double gValue) {
        this.gValue = gValue;
    }

    int getHeuristic() {
        return heuristic;
    }

    void setHeuristic(int heuristic) {
        this.heuristic = heuristic;
    }

    double getRhsValue() {
        return rhsValue;
    }

    void setRhsValue(double rhsValue) {
        this.rhsValue = rhsValue;
    }

    double getKey() {
        return key;
    }

    void setKey(double key) {
        this.key = key;
    }

    String getKeyForNodeManager() {
        return keyForNodeManager;
    }

    @Override
    public int hashCode() {
        return this.getKeyForNodeManager().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Node) {
            return this.keyForNodeManager.equals(((Node) o).getKeyForNodeManager());
        }
        return false;
    }

    @Override
    public String toString() {
        return (posX*DStar.BOARD_SIZE + posY + 1)
               + " " + getKey() + " " + getgValue() + " " + getRhsValue();
    }
}

class NodeManager {
    private static NodeManager nm;
    private Map<String, Node> nodes = new HashMap<>();
    static NodeManager getInstance() {
        if (nm == null) {
            nm = new NodeManager();
        }
        return nm;
    }
    void addNode(Node node) {
        nodes.put(node.getKeyForNodeManager(), node);
    }

    Node getNode(String key) {
        return nodes.get(key);
    }

    boolean containsNode(String key) {
        return nodes.containsKey(key);
    }

}