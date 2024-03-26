package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import util.FHeap.Node;

public class Prim {

    @SuppressWarnings("rawtypes")
    public static Graph getMst(Graph g, String root) {
        Graph t = new Graph();
        FHeap q = new FHeap();
        Map<String, Boolean> vis = new HashMap<String, Boolean>();
        Map<String, Integer> dist = new HashMap<String, Integer>();
        Map<String, String> anterior = new HashMap<String, String>();

        Iterable<String> vertices = g.vertices();
        for (String u : vertices) {
            vis.put(u, false);
            dist.put(u, 9999999);
            anterior.put(u, null);
        }
        dist.put(root, 0);
        q.enqueue(root, 0);

        while (!q.isEmpty()) {
            Node node = q.dequeueMin();
            String u = node.getId();
            int du = (int) node.getCost();

            if (vis.get(u) == false) {
                vis.put(u, true);
                if (!u.equals(root))
                    t.addEdge(u, anterior.get(u), du);

                Map<String, Integer> adj = g.adjacentTo(u, true);
                Iterator it = adj.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    String v = (String) pair.getKey();
                    int dv = (int) pair.getValue();

                    if (vis.get(v) == false && dv < dist.get(v)) {
                        dist.put(v, dv);
                        anterior.put(v, u);
                        q.enqueue(v, dv);
                    }
                }
            }
        }

        return t;
    }
}
