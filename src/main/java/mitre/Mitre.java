package mitre;

import eppstein.Eppstein;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import shioura.Shioura;
import util.Graph;
import util.Prim;

public class Mitre {

    /**
     * Main function for testing
     */
//	public static void main(String [] args) {
//		Graph g = new Graph();
//        g.addEdge("A", "B");
//        g.addEdge("A", "F");
//        g.addEdge("B", "G");
//        g.addEdge("B", "C");
//        g.addEdge("B", "F");
//        g.addEdge("C", "I");
//        g.addEdge("C", "E");
//        g.addEdge("C", "D");
//        g.addEdge("C", "H");
//        g.addEdge("D", "E");
//        g.addEdge("E", "K");
//        g.addEdge("F", "K");
//        g.addEdge("G", "I");
//        g.addEdge("I", "J");
//        g.addEdge("J", "H");
//
//        List<String> terminals = new ArrayList<String>();
//
//		terminals.add("J");
//		terminals.add("F");
//		terminals.add("A");
//		//terminals.add("D");
//
//		Mitre.execute(g, terminals);
//	}
    /**
     * Execute Mitre's algorithm to show all minimal steiner trees from graph G
     * and W being a subset of vertices from G
     */
    public static List<Graph> execute(Graph graph, List<String> terminals) {

        List<Graph> steinerTrees = new ArrayList<Graph>();
        Graph costsGraph = MitreUtils.floydWarshall(graph);

        int dsw = 999999;
        List<Graph> templates = new ArrayList<Graph>();

        if (terminals.size() == 0)
            return steinerTrees;
        else if (terminals.size() == 1) {
            Graph g = new Graph();
            g.addVertex(terminals.get(0));
            steinerTrees.add(g);
            return steinerTrees;
        } else if (terminals.size() == 2) {
            Graph grw = MitreUtils.constructGrwGraph(new HashSet<String>(), terminals, costsGraph);
            String root = grw.getAleatoryRoot();
            Graph minTree = Prim.getMst(grw, root);
            templates.add(minTree);
        } else {
            List<Set<String>> rSubSet = MitreUtils.getNonTerminalSubsets(terminals, graph);
            for (Set<String> sub : rSubSet) {
                Graph grw = MitreUtils.constructGrwGraph(sub, terminals, costsGraph);

                //get minimal spanning tree from graph Grw
                String root = grw.getAleatoryRoot();
                Graph minTree = Prim.getMst(grw, root);

                if (MitreUtils.validateMinTree(minTree, sub)) {
                    int treeWeight = MitreUtils.getMinTreeWeight(minTree);
                    if (treeWeight <= dsw) {
                        if (treeWeight < dsw) {
                            dsw = treeWeight;
                            templates.clear();
                        }
                        Eppstein epps = new Eppstein(grw, minTree, root);
                        Graph eg = epps.getEG();
                        //Set<Aresta> extras = new HashSet<>();
                        //extras.add(new Aresta("A", "B", 1));
                        Shioura shi = new Shioura(eg, root);
                        List<Graph> allmsts = shi.getAllTrees();
                        epps.convertTreesBack(allmsts);
                        templates.addAll(allmsts);
                    }
                }
            }
        }

        Ig ig = new Ig(costsGraph);
        ig.createLayers();
        ig.createN_uv(graph);

        //System.out.println("Number of templates = " + templates.size());
        for (Graph tree : templates)
            ig.enumerateSteiner(0, MitreUtils.getTreeEdges(tree), tree, steinerTrees);

//		System.out.println("Showing all steiner-trees found:");
//		System.out.println("total = " + steinerTrees.size() + "\n");
//		for(Graph tree : steinerTrees) {
//			System.out.println("steiner-tree\n"+tree);
//		}
        return steinerTrees;
    }

}
