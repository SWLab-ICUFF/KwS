package uff.ic.swlab.util.StainerTree;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.commons.math3.util.CombinatoricsUtils;

public class Graph {

    private final List<Edge> edges = new ArrayList<>();
    private final Map<String, Node> nodes = new HashMap<>();
    private Node lastNode = null;

    private class Edge {

        private String label = null;
        private String node1 = null;
        private String node2 = null;

        public Edge(String node1, String node2) {
            this.node1 = node1;
            this.node2 = node2;
        }

        public String getLabel() {
            return label;
        }

        public String getNode1() {
            return node1;
        }

        public String getNode2() {
            return node2;
        }

    }

    private class Node {

        private String label = null;
        private Map<Node, Integer> adjacents = new HashMap<>();

        public Node(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public void addAdjacent(Node node) {
            if (adjacents.get(node) == null)
                adjacents.put(node, 1);
            else
                adjacents.put(node, adjacents.get(node) + 1);
        }

        public void removeAdjacent(Node node) {
            if (adjacents.get(node) == 1)
                adjacents.remove(node);
            else
                adjacents.put(node, adjacents.get(node) - 1);
        }

        public Set<Node> adjacents() {
            return adjacents.keySet();
        }

        @Override
        public int hashCode() {
            return label.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj.getClass() == this.getClass())
                return label.equals(((Node) obj).label);
            else
                return false;
        }
    }

    public void addEdge(String node1, String node2) {
        Node n1 = nodes.get(node1);
        if (n1 == null) {
            n1 = new Node(node1);
            nodes.put(node1, n1);
        }

        Node n2 = nodes.get(node2);
        if (n2 == null) {
            n2 = new Node(node2);
            nodes.put(node2, n2);
        }
        n1.addAdjacent(n2);
        n2.addAdjacent(n1);

        lastNode = n2;
        edges.add(new Edge(node1, node2));
    }

    public void addEdges(int[] indexes) {
        for (int index : indexes) {
            Edge edge = edges.get(index);
            Node n1 = nodes.get(edge.node1);
            if (n1 == null) {
                n1 = new Node(edge.node1);
                nodes.put(edge.node1, n1);
            }

            Node n2 = nodes.get(edge.node2);
            if (n2 == null) {
                n2 = new Node(edge.node2);
                nodes.put(edge.node2, n2);
            }
            n1.addAdjacent(n2);
            n2.addAdjacent(n1);
        }
    }

    public void removeEdge(int index) {
        Node n1 = nodes.get(edges.get(index).getNode1());
        Node n2 = nodes.get(edges.get(index).getNode2());
        n1.removeAdjacent(n2);
        n2.removeAdjacent(n1);
    }

    public void removeEdges(int[] indexes) {
        for (int index : indexes)
            removeEdge(index);
    }

    public void clear() {
        for (Node node : nodes.values())
            node.adjacents = new HashMap<>();
    }

    public void reset() {
        for (Node node : nodes.values())
            node.adjacents = new HashMap<>();

        for (Edge edge : edges) {
            Node n1 = nodes.get(edge.node1);
            if (n1 == null) {
                n1 = new Node(edge.node1);
                nodes.put(edge.node1, n1);
            }

            Node n2 = nodes.get(edge.node2);
            if (n2 == null) {
                n2 = new Node(edge.node2);
                nodes.put(edge.node2, n2);
            }
            n1.addAdjacent(n2);
            n2.addAdjacent(n1);
        }
    }

    public int edges() {
        return edges.size();
    }

    public int vertices() {
        return nodes.size();
    }

    public boolean isConnected() {
        Set<Node> visitedNodes = new HashSet<>();
        Set<Node> toBeVisitedNodes = new HashSet<>();

        toBeVisitedNodes.add(lastNode);
        while (!toBeVisitedNodes.isEmpty()) {
            Set<Node> toBeVisitedNodes2 = new HashSet<>();
            for (Node visitedNode : toBeVisitedNodes) {
                visitedNodes.add(visitedNode);
                for (Node node : visitedNode.adjacents())
                    if (!visitedNodes.contains(node))
                        toBeVisitedNodes2.add(node);
            }
            toBeVisitedNodes = toBeVisitedNodes2;
        }
        if (visitedNodes.size() == nodes.size())
            return true;
        return false;
    }

    public static Integer ComputeSubGraps(Graph g) {
        int r = g.vertices() - 1, total = 0, count = 0;
        do {
            count = 0;
            Iterator<int[]> combinations = CombinatoricsUtils.combinationsIterator(g.edges(), r);
            System.out.println("Gerou combinacoes");
            while (combinations.hasNext()) {
                int[] indexes = combinations.next();
                g.clear();
                g.addEdges(indexes);
                if (g.isConnected()) {
                    System.out.println(Arrays.toString(indexes));
                    count++;
                    total++;
                }
            }
            r++;
        } while (count == 0);
        return total;
    
    }

    public static void main(String[] args) {
        Graph g = new Graph();
        g.addEdge("A", "C");
        g.addEdge("A", "E");
        g.addEdge("E", "B");
        g.addEdge("E", "D");
        g.addEdge("E", "C");

        int r = g.vertices() - 1, total = 0, count = 0;
        do {
            count = 0;
            Iterator<int[]> combinations = CombinatoricsUtils.combinationsIterator(g.edges(), r);
            while (combinations.hasNext()) {
                int[] indexes = combinations.next();
                g.clear();
                g.addEdges(indexes);
                if (g.isConnected()) {
                    System.out.println(Arrays.toString(indexes));
                    count++;
                    total++;
                }
            }
            r++;
        } while (count == 0);
        System.out.println(total);
    }
}
