package mitre;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import util.Graph;

class Ig {

    private HashMap<String, Set<String>> ig;
    private HashMap<String, Set<String>> layers;
    private HashMap<String, Set<String>> n_uv;
    private Graph costsGraph;

    @SuppressWarnings("rawtypes")
    public Ig(Graph nCostsGraph) {
        this.costsGraph = nCostsGraph;
        zeroLoopEdges();

        ig = new HashMap<String, Set<String>>();

        //get all pairs u-v
        Iterable<String> vertices = costsGraph.vertices();
        for (String u : vertices) {

            HashMap<String, Integer> adj = costsGraph.adjacentTo(u, true);
            Iterator it = adj.entrySet().iterator();

            while (it.hasNext()) {

                Map.Entry pair = (Map.Entry) it.next();
                String v = (String) pair.getKey();

                int uv_cost = (Integer) pair.getValue();
                Set<String> subset = new HashSet<String>();

                //with pair u,v make this operation
                for (String k : vertices) {
                    int uk_cost = costsGraph.adjacentTo(u, true).get(k);
                    int kv_cost = costsGraph.adjacentTo(k, true).get(v);
                    if (uv_cost == uk_cost + kv_cost)
                        subset.add(k);
                }
                ig.put(u + "-" + v, subset);
                //System.out.println(u+"-"+v+"->"+subset);
            }
        }
        //System.exit(0);
    }

    /**
     * Put cost 0 in edges u-u on costsGraph
     */
    private void zeroLoopEdges() {
        Iterable<String> vertices = costsGraph.vertices();
        for (String v : vertices)
            this.costsGraph.adjacentTo(v, true).put(v, 0);
    }

    /**
     * Create layers 0 <= l <= dg(u,v)
     */
    @SuppressWarnings("rawtypes")
    public void createLayers() {
        layers = new HashMap<String, Set<String>>();

        Iterator it = ig.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String aux = (String) pair.getKey();
            String[] parts = aux.split("-");
            String u = parts[0];
            String v = parts[1];
            int dg_u_v = costsGraph.adjacentTo(u, true).get(v);
            Set<String> subset = ig.get(aux);
            for (int l = 0; l <= dg_u_v; l++) {
                Set<String> z_sub = new HashSet<String>();
                for (String z : subset)
                    if (costsGraph.adjacentTo(u, true).get(z) == l)
                        z_sub.add(z);
                String code = u + "-" + v + "-" + l;
                layers.put(code, z_sub);
                //System.out.println(code + " -> " + z_sub);
            }
        }
        //System.exit(0);
    }

    /**
     * Create all Nu-v(z) based on layers
     */
    @SuppressWarnings("rawtypes")
    public void createN_uv(Graph grafo) {
        n_uv = new HashMap<String, Set<String>>();

        Iterator it = ig.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String aux = (String) pair.getKey();
            String[] parts = aux.split("-");
            String u = parts[0];
            String v = parts[1];
            int dg_u_v = costsGraph.adjacentTo(u, true).get(v);
            for (int l = 0; l < dg_u_v; l++) {
                Set<String> subset = layers.get(u + "-" + v + "-" + l);
                Set<String> next_subset = layers.get(u + "-" + v + "-" + (l + 1));
                for (String z : subset) {
                    Set<String> neighbors = new HashSet<String>();
                    for (String w : next_subset)
                        if (grafo.hasEdge(z, w))
                            neighbors.add(w);
                    n_uv.put(u + "-" + v + "-" + z, neighbors);
                    //System.out.println(u+"-"+v+"-"+z + " -> " + neighbors);
                }
            }
        }
        //System.exit(0);
    }

    /**
     * Replace edge u-v on tree by path P
     */
    private void replaceEdgesGo(Graph tree, String u, String v, List<String> path) {
        tree.removeEdgeBoth(u, v);
        int size = path.size();
        for (int i = 0; i < size - 1; i++)
            tree.addEdge(path.get(i), path.get(i + 1));
    }

    /**
     * Replace path P on tree by edge u-v with original cost
     */
    private void replaceEdgesBack(Graph tree, String u, String v, int cost, List<String> path) {
        int size = path.size();
        for (int i = 0; i < size - 1; i++) {
            tree.removeEdgeBoth(path.get(i), path.get(i + 1));

            //remove alone vertices from middle of path
            if (i > 0 && i < size - 1)
                tree.removeVertex(path.get(i));
        }
        tree.addEdge(u, v, cost);
    }

    /**
     * Enumarate all possible steiner trees on a minimal spanning tree
     */
    public void enumerateSteiner(int j, List<String> edges, Graph tree, List<Graph> steinerTrees) {
        String[] parts = edges.get(j).split("-");
        String u = parts[0];
        String v = parts[1];

        int dg_u_v = costsGraph.adjacentTo(u, true).get(v);
        int dl[] = new int[dg_u_v];
        for (int l = 0; l < dg_u_v; l++)
            dl[l] = 0;

        List<String> path_p = new ArrayList<String>();
        path_p.add(u);

        while (!path_p.isEmpty()) {
            int l = path_p.size() - 1;
            String wl = path_p.get(l);
            String code = u + "-" + v + "-" + wl;

            if (l == dg_u_v) {

                replaceEdgesGo(tree, u, v, path_p);
                if (j < edges.size() - 1)
                    this.enumerateSteiner(j + 1, edges, tree, steinerTrees);
                else
                    steinerTrees.add(new Graph(tree));

                replaceEdgesBack(tree, u, v, dg_u_v, path_p);
                path_p.remove(l);
                path_p.remove(l - 1);
                dl[l - 1] = 0;

            } else if (dl[l] < n_uv.get(code).size()) {
                List<String> subset = new ArrayList<String>(n_uv.get(code));
                dl[l] += 1;
                String wl_1 = subset.get(dl[l] - 1);
                path_p.add(wl_1);
            } else {
                path_p.remove(wl);
                dl[l] = 0;
            }
        }
    }

}
