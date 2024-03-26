package util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PrimClassic {

    @SuppressWarnings("rawtypes")
    private static String getMenor(Map<String, Boolean> vis, Map<String, Integer> dist) {
        int custo = 999999999;
        String menor = "-1";
        Iterator it = dist.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            String u = (String) pair.getKey();
            int du = (int) pair.getValue();
            if (du < custo && vis.get(u) == false) {
                menor = u;
                custo = du;
            }
        }
        return menor;
    }

    @SuppressWarnings("rawtypes")
    public static Graph getMst(Graph g, String root) {
        Graph t = new Graph();

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

        while (true) {
            String u = PrimClassic.getMenor(vis, dist);
            if (u.equals("-1"))
                break;

            int du = dist.get(u);

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
                }
            }
        }

        return t;
    }
}
