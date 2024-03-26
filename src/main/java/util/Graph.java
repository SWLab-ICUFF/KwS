package util;

/**
 * ***********************************************************************
 * Compilation: javac Graph.java Dependencies: ST.java SET.java In.java
 *
 * Undirected graph data type implemented using a symbol table whose keys are
 * vertices (String) and whose values are sets of neighbors (SET of Strings).
 *
 * Remarks ------- - Parallel edges are not allowed - Self-loop are allowed -
 * Adjacency lists store many different copies of the same String. You can use
 * less memory by interning the strings.
 *
 ************************************************************************
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The <tt>Graph</tt> class represents an undirected graph of vertices with
 * string names. It supports the following operations: add an edge, add a
 * vertex, get all of the vertices, iterate over all of the neighbors adjacent
 * to a vertex, is there a vertex, is there an edge between two vertices.
 * Self-loops are permitted; parallel edges are discarded.
 * <p>
 * For additional documentation, see
 * <a href="http://introcs.cs.princeton.edu/45graph">Section 4.5</a> of
 * <i>Introduction to Programming in Java: An Interdisciplinary Approach</i> by
 * Robert Sedgewick and Kevin Wayne.
 */
public class Graph {

    // symbol table: key = string vertex, value = set of neighboring vertices
    private HashMap<String, HashMap<String, Integer>> st;

    // number of edges
    private int E;

    /**
     * Create an empty graph with no vertices or edges.
     */
    public Graph() {
        st = new HashMap<String, HashMap<String, Integer>>();
    }

    /*
      Create deep copy of another graph
     */
    public Graph(Graph g) {
        st = new HashMap<String, HashMap<String, Integer>>();
        Iterable<String> vertices = g.vertices();
        for (String u : vertices) {
            HashMap<String, Integer> internalHash = g.adjacentTo(u, true);
            st.put(u, new HashMap<String, Integer>(internalHash));
        }
    }

    /**
     * Number of vertices.
     */
    public int V() {
        return st.size();
    }

    /**
     * Number of edges.
     */
    public int E() {
        return E;
    }

    /**
     *
     * Return first Vertex to be used as root
     */
    public String getAleatoryRoot() {
        return st.entrySet().iterator().next().getKey();
    }

    /**
     * Degree of this vertex.
     */
    public int degree(String v) {
        if (!st.containsKey(v))
            throw new RuntimeException(v + " is not a vertex");
        else
            return st.get(v).size();
    }

    /**
     * Add edges v-w and w-v to this graph (if it is not already an edge) with
     * cost 1
     */
    public void addEdge(String v, String w) {
        if (!hasEdge(v, w))
            E++;
        if (!hasVertex(v))
            addVertex(v);
        if (!hasVertex(w))
            addVertex(w);
        st.get(v).put(w, 1);
        st.get(w).put(v, 1);
    }

    /**
     * Add edge v-w and w-v to this graph (if it is not already an edge) with a
     * cost associated
     */
    public void addEdge(String v, String w, int cost) {
        if (!hasEdge(v, w))
            E++;
        if (!hasVertex(v))
            addVertex(v);
        if (!hasVertex(w))
            addVertex(w);
        st.get(v).put(w, cost);
        st.get(w).put(v, cost);
    }

    /**
     * Add edge v-w to this graph (if it is not already an edge) with cost 1
     */
    public void addEdgeUni(String v, String w) {
        if (!hasEdge(v, w))
            E++;
        if (!hasVertex(v))
            addVertex(v);
        if (!hasVertex(w))
            addVertex(w);
        st.get(v).put(w, 1);
    }

    /**
     * Add edge v-w to this graph (if it is not already an edge) with a cost
     * associated
     */
    public void addEdgeUni(String v, String w, int cost) {
        if (!hasEdge(v, w))
            E++;
        if (!hasVertex(v))
            addVertex(v);
        if (!hasVertex(w))
            addVertex(w);
        st.get(v).put(w, cost);
    }

    /**
     * Remove edges v-w (if exists)
     */
    public void removeEdge(String v, String w) {
        if (hasEdge(v, w)) {
            st.get(v).remove(w);
            E--;
        }
    }

    /**
     * Remove edges v-w and w-v (if they exist)
     */
    public void removeEdgeBoth(String v, String w) {
        int old_E = E;
        if (hasEdge(v, w)) {
            st.get(v).remove(w);
            E--;
        }
        if (hasEdge(w, v)) {
            st.get(w).remove(v);
            E--;
        }
        if ((old_E + 1) != E)
            E++;
    }

    /**
     * Remove vertex v from graph CAUTION!! this method will not remove the
     * edges that points to v
     */
    public void removeVertex(String v) {
        if (hasVertex(v)) {
            Iterable<String> adj = adjacentTo(v);
            for (String u : adj)
                removeEdgeBoth(u, v);
            st.remove(v);
        }
    }

    /**
     * Add vertex v to this graph (if it is not already a vertex)
     */
    public void addVertex(String v) {
        if (!hasVertex(v))
            st.put(v, new HashMap<String, Integer>());
    }

    /**
     * Return the set of vertices as an Iterable.
     */
    public Iterable<String> vertices() {
        return new ArrayList<String>(st.keySet());
    }

    /**
     * Return the set of vertices as an Iterable.
     */
    public ArrayList<String> verticesList() {
        return new ArrayList<String>(st.keySet());
    }

    /**
     * Return the set of neighbors of vertex v as in Iterable.
     */
    public Iterable<String> adjacentTo(String v) {
        // return empty set if vertex isn't in graph
        if (!hasVertex(v))
            return new ArrayList<String>();
        else
            return new ArrayList<String>(st.get(v).keySet());
    }

    /**
     * Return the set of neighbors of vertex v as an HashMap with costs
     */
    public HashMap<String, Integer> adjacentTo(String v, boolean withcosts) {
        // return empty set if vertex isn't in graph
        if (!hasVertex(v))
            return new HashMap<String, Integer>();
        else
            return st.get(v);
    }

    /**
     * Is v a vertex in this graph?
     */
    public boolean hasVertex(String v) {
        return st.containsKey(v);
    }

    /**
     * Is v-w an edge in this graph?
     */
    public boolean hasEdge(String v, String w) {
        if (!hasVertex(v))
            return false;
        return st.get(v).containsKey(w);
    }

    public int getCost(String u, String v) {
        if (!hasEdge(u, v))
            return -1;
        return st.get(u).get(v);
    }

    public void copyAdjacency(String u, String v) {
        Iterable<String> adj = adjacentTo(u);
        for (String w : adj)
            addEdge(v, w);
    }

    /*
      Return a string representation of the graph.
     */
    public String toString() {
        String s = "";
        Iterable<String> vertices = vertices();
        for (String v : vertices) {
            s += v + ": ";
            Iterable<String> adj = adjacentTo(v);
            for (String w : adj)
                s += w + " ";
            s += "\n";
        }
        return s;
    }

    /*
      Return a string representation of the graph showing edge costs
     */
    @SuppressWarnings("rawtypes")
    public String toStringWithCosts() {
        String s = "";
        Iterable<String> vertices = vertices();
        for (String v : vertices) {
            s += v + ": ";
            Iterator it = adjacentTo(v, true).entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                s += pair.getKey() + "->" + pair.getValue() + " ";
            }
            s += "\n";
        }
        return s;
    }

}
