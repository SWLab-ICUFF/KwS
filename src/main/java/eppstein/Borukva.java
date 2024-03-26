package eppstein;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Borukva {

    private Map<String, String> pai;
    private Map<String, Integer> rank;
    private int counter;
    public String nbroot;

    public Borukva() {
        pai = new HashMap<String, String>();
        rank = new HashMap<String, Integer>();
        counter = 0;
    }

    private String generateString() {
        counter++;
        return "t" + counter;
    }

    private String find(String i) {
        if (!pai.get(i).equals(i))
            pai.put(i, find(pai.get(i)));

        return pai.get(i);
    }

    private void Union(String x, String y) {
        String xroot = find(x);
        String yroot = find(y);

        if (rank.get(xroot) < rank.get(yroot))
            pai.put(xroot, yroot);
        else if (rank.get(xroot) > rank.get(yroot))
            pai.put(yroot, xroot);
        else {
            pai.put(yroot, xroot);
            int aux = rank.get(xroot) + 1;
            rank.put(xroot, aux);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public Graph borukva(Graph t) {
        Graph b = new Graph();

        Map<String, String> linker = new HashMap<String, String>();
        Map<String, Integer> lowcost = new HashMap<String, Integer>();
        Set<String> trees = new HashSet<String>();

        List<String> vertices = t.verticesList();

        for (String u : vertices) {
            pai.put(u, u);
            rank.put(u, 0);
            trees.add(u);
            linker.put(u, u);
            lowcost.put(u, 9999999);
        }

        while (trees.size() > 1) {
            Map<String, Integer> menor_custo = new HashMap<String, Integer>();
            Map<String, String> menor = new HashMap<String, String>();

            for (String u : vertices) {
                String set1 = find(u);
                menor_custo.put(set1, 9999999);
            }

            for (String u : vertices) {
                String set1 = find(u);
                Iterable<String> adj = t.adjacentTo(u);
                for (String v : adj) {
                    String set2 = find(v);
                    int custo = t.getCost(u, v);
                    if (set1.equals(set2))
                        continue;
                    if (custo < menor_custo.get(set1)) {
                        menor_custo.put(set1, custo);
                        menor.put(set1, v);
                    }
                }
            }
            Set<String> checado = new HashSet<String>();
            for (String u : vertices) {
                String set1 = find(u);

                if (checado.contains(set1))
                    continue;
                checado.add(set1);
                int custo = menor_custo.get(set1);
                String v = menor.get(set1);
                String set2 = find(v);
                Union(set1, set2);
                lowcost.put(linker.get(u), Math.min(lowcost.get(linker.get(u)), custo));
                lowcost.put(linker.get(v), Math.min(lowcost.get(linker.get(v)), custo));
            }

            HashMap<String, Set<String>> meuMapSet = new HashMap<String, Set<String>>();
            for (String u : vertices) {
                String p = find(u);
                if (meuMapSet.get(p) == null)
                    meuMapSet.put(p, new HashSet<String>());
                meuMapSet.get(p).add(linker.get(u));
            }
            //System.out.println("mapset -> " + meuMapSet);
            trees.clear();

            HashMap<String, String> novos_linkers = new HashMap<String, String>();
            Iterator it = meuMapSet.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                String set1 = (String) pair.getKey();
                Set<String> A = (Set) pair.getValue();
                String novo_vertice = generateString();
                novos_linkers.put(set1, novo_vertice);
                nbroot = novo_vertice;
                trees.add(novo_vertice);
                for (String v : A)
                    b.addEdge(novo_vertice, v, lowcost.get(v));
                lowcost.put(novo_vertice, 999999);
            }

            for (String u : vertices) {
                String set1 = find(u);
                linker.put(u, novos_linkers.get(set1));;
            }
        }

        return b;
    }
}
