package mitre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import util.Graph;

class MitreUtils {

    /**
     * Retrieve a complete graph, with all minimum path costs from all vertices
     * to all others Algorithm elaborated by Floyd-Warshall
     */
    public static Graph floydWarshall(Graph g) {

        Graph costsGraph = new Graph(g);

        Iterable<String> vertices = costsGraph.vertices();

        for (String sk : vertices)
            for (String si : vertices)
                for (String sj : vertices)
                    if (!si.equals(sj)) {
                        int ik_cost = 999999, kj_cost = 999999, ij_cost = 999999;
                        if (costsGraph.adjacentTo(si, true).get(sk) != null)
                            ik_cost = costsGraph.adjacentTo(si, true).get(sk);
                        if (costsGraph.adjacentTo(sk, true).get(sj) != null)
                            kj_cost = costsGraph.adjacentTo(sk, true).get(sj);
                        if (costsGraph.adjacentTo(si, true).get(sj) != null)
                            ij_cost = costsGraph.adjacentTo(si, true).get(sj);

                        int min = Math.min(ij_cost, (ik_cost + kj_cost));
                        costsGraph.adjacentTo(si, true).put(sj, min);
                    } else
                        costsGraph.adjacentTo(si, true).put(sj, 999999);
        return costsGraph;
    }

    /**
     * Recursive function to get all subsets from size 0 to k
     */
    private static void getSubsets(List<String> superSet, int k, int idx, Set<String> current, List<Set<String>> solution) {

        if (!solution.contains(current) && current.size() > 0)
            solution.add(new HashSet<>(current));
        //successful stop clause
        if (current.size() == k)
            return;
        //unseccessful stop clause
        if (idx == superSet.size())
            return;
        String x = superSet.get(idx);
        current.add(x);
        //"guess" x is in the subset
        getSubsets(superSet, k, idx + 1, current, solution);
        current.remove(x);
        //"guess" x is not in the subset
        getSubsets(superSet, k, idx + 1, current, solution);
    }

    /**
     * Get all possibles subsets of a set, with k max-size
     */
    private static List<Set<String>> getSubsets(List<String> superSet, int k) {
        List<Set<String>> res = new ArrayList<>();
        getSubsets(superSet, k, 0, new HashSet<String>(), res);
        return res;
    }

    /**
     * Get all possibles subsets of nonterminals vertices with |R| <= |W|-2
     */
    public static List<Set<String>> getNonTerminalSubsets(List<String> terminals, Graph g) {
        Iterable<String> vertices = g.vertices();
        List<String> nonterminals = new ArrayList<String>();
        for (String v : vertices)
            if (!terminals.contains(v))
                nonterminals.add(v);
        int k = terminals.size() - 2;

        return getSubsets(nonterminals, k);
    }

    /**
     * Construct a complete graph Grw using terminals and nonterminals vertices
     * applying costs calculated on floyd-warshall's algorithm
     */
    public static Graph constructGrwGraph(Set<String> subset, List<String> terminals, Graph costsGraph) {
        List<String> nodes = new ArrayList<String>(terminals);
        nodes.addAll(subset);

        Graph grw = new Graph();
        for (String i : nodes)
            for (String j : nodes)
                if (!i.equals(j)) {
                    int cost = costsGraph.getCost(i, j);
                    grw.addEdge(i, j, cost);
                }

        return grw;
    }

    /**
     * Validate if minimal spanning tree does not has vertices from R with
     * degree smaller than 2
     */
    public static boolean validateMinTree(Graph tree, Set<String> subset) {
        List<String> nodes = new ArrayList<String>(subset);
        for (String v : nodes)
            if (tree.degree(v) < 2)
                return false;
        return true;
    }

    /**
     * Retrieve the weight of a minimal spanning tree
     */
    @SuppressWarnings("rawtypes")
    public static int getMinTreeWeight(Graph tree) {
        int total = 0;
        Graph auxTree = new Graph(tree);

        Iterable<String> vertices = auxTree.vertices();
        for (String u : vertices) {
            HashMap<String, Integer> adj = auxTree.adjacentTo(u, true);
            Iterator it = adj.entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String v = (String) pair.getKey();
                int w = (Integer) pair.getValue();
                total += w;
                auxTree.removeEdge(v, u);
            }
        }

        return total;
    }

    /**
     * Retrieve a list of all edges of a tree
     */
    public static List<String> getTreeEdges(Graph tree) {
        List<String> edges = new ArrayList<String>();
        Iterable<String> vertices = tree.verticesList();
        for (String u : vertices) {
            Iterable<String> adj = tree.adjacentTo(u);
            for (String v : adj)
                if (!edges.contains(v + "-" + u))
                    edges.add(u + "-" + v);
        }
        return edges;
    }

}
