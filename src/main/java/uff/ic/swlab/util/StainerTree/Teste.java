package uff.ic.swlab.util.StainerTree;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import static uff.ic.swlab.util.StainerTree.CountStainerTree.CreateGraphbySG;
import static uff.ic.swlab.util.StainerTree.CountStainerTree.ReadDataset;
import static uff.ic.swlab.util.StainerTree.CountStainerTree.createRepresentationURI;
import static uff.ic.swlab.util.StainerTree.CountStainerTree.getSG;

public class Teste {

//    public static void main(String[] args) {
//        for (int i = 1; i < 10; i++) {
//            Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(700, i);
//            while (iterator.hasNext()) {
//                final int[] combination = iterator.next();
//                for (int j = 0; j < combination.length; j++)
//                    System.out.print(combination[j] + ",");
//                System.out.println("");
//
//            }
//        }
//    }
    public static Graph CreateGraphbySG(Dataset dataset, String sg, HashMap<String, String> mapURI) {
        Map<String, String> edges = new HashMap<>();
        Graph graph = new Graph();
        String queryString = String.format("PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "\n"
                + "SELECT ?s ?p ?o\n"
                + "WHERE{\n"
                + "  graph <%1$s>{\n"
                + "  		?s ?p ?o.\n"
                + "  }\n"
                + "  FILTER(!isLiteral(?o))\n"
                + "}\n", sg);
        QueryExecution q = QueryExecutionFactory.create(queryString, dataset);
        ResultSet result = q.execSelect();
        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            String s = String.valueOf(soln.get("s"));
            String o = String.valueOf(soln.get("o"));
            if (!(edges.containsKey(s) && edges.get(s).equals(o))
                    && !(edges.containsKey(o) && edges.get(o).equals(s))) {

                String numberRepresentationS = mapURI.get(s);
                String numberRepresentationO = mapURI.get(o);
                graph.addEdge(numberRepresentationS, numberRepresentationO);
                edges.put(s, o);
            }
        }
        q.close();

        return graph;
    }

    public static void main(String[] args) {

        String nameDataset = "Mondial";
        File folder = new File(String.format("./src/main/resources/benchmarks/ESWC2021/%1$s", nameDataset));
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);

        Integer count = 1;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].toString().endsWith(".nq.gz")) {
                if (count == 15) {
                    System.out.println("-----------------------" + count + "-----------------------------");
                    Dataset dataset = ReadDataset(listOfFiles[i].toString());
                    ArrayList<String> listSG = getSG(dataset);
                    System.out.println("SG:" + listSG.size());
                    Integer total = 0;
                    for (String sg : listSG) {
                        HashMap<String, String> mapURI = createRepresentationURI(dataset, sg);
                        System.out.println("Criou representacao");
                        Graph graph = CreateGraphbySG(dataset, sg, mapURI);
                        int total_subgraphs = Graph.ComputeSubGraps(graph);
                        total = total + total_subgraphs;
                    }
                    System.out.println(total);
                }
                count++;
            }
        }

    }

}
