package shioura;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import util.Graph;

class _Aux {

    private Edge[] t0;
    private LinkedList<Edge> edges;
    private Map<String, Integer> dictionary;
    private Map<Integer, String> inv_dictionary;
    private Graph dfst, originalGraph;
    private int dfscount;

    public _Aux() {
        this.edges = new LinkedList<Edge>();
        dictionary = new HashMap<String, Integer>();
        inv_dictionary = new HashMap<Integer, String>();
        dfst = new Graph();
    }

    public LinkedList<Edge> mountTree(Graph grafo, String root) {
        this.originalGraph = grafo;
        LinkedList<Edge> leaves = new LinkedList<Edge>();
        Map<String, Boolean> visitados = new HashMap<String, Boolean>();
        ArrayList<String> vertices = grafo.verticesList();
        this.t0 = new Edge[vertices.size()];
        this.t0[0] = null;
        for (String u : vertices)
            visitados.put(u, false);
        dictionary.put(root, 0);
        inv_dictionary.put(0, root);
        dfscount = 1;
        dfs(grafo, root, 0, visitados, leaves);
        return leaves;
    }

    public LinkedList<Edge> getNonTreeEdges(Graph g, String root) {
        ArrayList<String> vertices = g.verticesList();
        Set<Edge> nonTreeEdges = new HashSet<Edge>();
        Map<String, Boolean> visitados = new HashMap<String, Boolean>();
        for (String u : vertices)
            visitados.put(u, false);
        //aqui faz BFS
        Queue<String> q = new LinkedBlockingQueue<String>();
        q.add(root);
        while (!q.isEmpty()) {
            String u = q.poll();
            visitados.put(u, true);
            Iterable<String> adj = g.adjacentTo(u);
            for (String v : adj)
                if (visitados.get(v) == false) {
                    q.add(v);
                    if (!dfst.hasEdge(u, v))
                        nonTreeEdges.add(new Edge(dictionary.get(u), dictionary.get(v)));
                }
        }
        return new LinkedList<Edge>(nonTreeEdges);
    }

    public Edge getF(int gplus) {
        return t0[gplus];
    }

    public void changeEdge(Edge ek, Edge g) {
        int cost_g = originalGraph.getCost(inv_dictionary.get(g.u), inv_dictionary.get(g.v));
        dfst.removeEdgeBoth(inv_dictionary.get(ek.u), inv_dictionary.get(ek.v));
        dfst.addEdge(inv_dictionary.get(g.u), inv_dictionary.get(g.v), cost_g);
    }

    public boolean hasEdge(String a, String b) {
        return dfst.hasEdge(a, b);
    }

    public Graph getActualTreeCopy() {
        return new Graph(dfst);
    }

    public LinkedList<Edge> getTreeEdges() {
        return edges;
    }

    private void dfs(Graph grafo, String u, int u_i, Map<String, Boolean> visitados, LinkedList<Edge> leaves) {
        visitados.put(u, true);

        Iterable<String> adj = grafo.adjacentTo(u);
        for (String v : adj)
            if (!visitados.get(v)) {
                int v_i = dfscount++;
                dictionary.put(v, v_i);
                inv_dictionary.put(v_i, v);
                Edge e = new Edge(u_i, v_i);
                dfst.addEdge(u, v, grafo.getCost(u, v));
                t0[v_i] = e;
                edges.add(e);
                leaves.add(e);
                dfs(grafo, v, v_i, visitados, leaves);
            }
    }
}
