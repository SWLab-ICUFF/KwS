package eppstein;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

class Schieber {

    private int n, l;
    private String root;
    private Graph t;
    private int order;
    private Map<String, Boolean> vis;
    private Map<String, Integer> preorder, sizeSubTree, inlabel, ascendent, level;
    private Map<String, String> parent;
    private Map<Integer, String> head;

    public Schieber(Graph t, String root) {
        this.root = root;
        this.n = t.V();
        l = log2(n);
        this.t = t;
        vis = new HashMap<String, Boolean>();
        preorder = new HashMap<String, Integer>();
        sizeSubTree = new HashMap<String, Integer>();
        inlabel = new HashMap<String, Integer>();
        ascendent = new HashMap<String, Integer>();
        level = new HashMap<String, Integer>();
        parent = new HashMap<String, String>();
        head = new HashMap<Integer, String>();
        clearVis();
        order = 1;
        parent.put(root, null);

        schieberDfs(root);

        schieberBfs();
    }

    public String queryLCA(String x, String y) {
        int max = (int) (Math.pow(2, l + 1) - 1);

        if (inlabel.get(x) == inlabel.get(y)) {
            if (level.get(x) <= level.get(y))
                return x;
            return y;
        } else {
            int i = log2(inlabel.get(x) ^ inlabel.get(y));
            int common = ascendent.get(x) & ascendent.get(y);
            int common_i = (common >> i) << i;
            int j = log2(common_i - (common_i & (common_i - 1)));
            int inlabel_z = (((inlabel.get(x) >> (j + 1)) << 1) | 1) << j;
            String x1, y1;
            if (inlabel_z == inlabel.get(x))
                x1 = x;
            else {
                int k = log2(ascendent.get(x) & (max >> (l - j + 1)));
                int inw = (((inlabel.get(x) >> (k + 1)) << 1) | 1) << k;
                x1 = parent.get(head.get(inw));
            }
            if (inlabel_z == inlabel.get(y))
                y1 = y;
            else {
                int k = log2(ascendent.get(y) & (max >> (l - j + 1)));
                int inw = (((inlabel.get(y) >> (k + 1)) << 1) | 1) << k;
                y1 = parent.get(head.get(inw));
            }

            if (level.get(x1) <= level.get(y1))
                return x1;
            return y1;
        }
    }

    private void clearVis() {
        Iterable<String> vertices = t.vertices();
        for (String u : vertices)
            vis.put(u, false);
    }

    private int log2(int x) {
        return (int) Math.floor(Math.log10(x) / Math.log10(2));
    }

    private int schieberDfs(String u) {
        vis.put(u, true);
        preorder.put(u, order++);
        Iterable<String> adj = t.adjacentTo(u);
        int tamanhoSubTree = t.degree(u) - 1;
        for (String v : adj)
            if (vis.get(v) == false)
                tamanhoSubTree += schieberDfs(v);
        sizeSubTree.put(u, tamanhoSubTree + 1);
        if (u.equals(root))
            sizeSubTree.put(u, tamanhoSubTree + 2);
        int a = preorder.get(u);
        int b = preorder.get(u) + sizeSubTree.get(u) - 1;
        int i = log2((a - 1) ^ b);
        int aux = (b >> i) << i;
        inlabel.put(u, aux);
        return tamanhoSubTree;
    }

    private void schieberBfs() {
        Queue<String> q = new LinkedBlockingQueue<String>();
        q.add(root);
        clearVis();

        ascendent.put(root, (1 << log2(n)));
        level.put(root, 0);

        while (!q.isEmpty()) {
            String u = q.poll();
            vis.put(u, true);
            Iterable<String> adj = t.adjacentTo(u);
            for (String v : adj)
                if (vis.get(v) == false) {
                    parent.put(v, u);
                    int aux = ascendent.get(parent.get(v)) | (inlabel.get(v) & (-inlabel.get(v)));
                    ascendent.put(v, aux);
                    if (inlabel.get(v) != inlabel.get(parent.get(v)))
                        head.put(inlabel.get(v), v);
                    level.put(v, level.get(parent.get(v)) + 1);
                    q.add(v);
                }
        }
    }

    public Map<String, Integer> getLevel() {
        return level;
    }

    public Map<String, String> getParents() {
        return parent;
    }

}
