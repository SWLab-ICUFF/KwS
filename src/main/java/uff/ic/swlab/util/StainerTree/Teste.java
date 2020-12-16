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
import org.apache.jena.riot.RDFDataMgr;

public class Teste {

//    public static void main(String[] args) {
//        for (int i = 1; i < 10; i++) {
//            System.out.println(i);
//            Iterator<int[]> iterator = CombinatoricsUtils.combinationsIterator(700, i);
//            while (iterator.hasNext()) {
//                final int[] combination = iterator.next();
//                //System.out.println(Arrays.toString(combination));
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

    public static ArrayList<String> getSG(Dataset dataset) {

        ArrayList<String> listSG = new ArrayList<>();
        String queryString = "SELECT DISTINCT ?sg\n"
                + "                  			WHERE{\n"
                + "                    			graph ?sg{\n"
                + "                    				?s ?p ?o.\n"
                + "    								?s <urn:vocab:kws:csScore> ?score.\n"
                + "    								?s <urn:vocab:kws:ssScore> ?score2.\n"
                + "                    			}\n"
                + "                   			 	FILTER(regex(str(?sg), \"urn:graph:kws:[0-9]{3}:sol\"))\n"
                + "  								FILTER(?score = 1.0)\n"
                + "  								FILTER(?score2 <= 0.4)\n"
                + "                  			}";

        QueryExecution q = QueryExecutionFactory.create(queryString, dataset);
        ResultSet result = q.execSelect();
        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            String sgURI = String.valueOf(soln.get("sg"));
            listSG.add(sgURI);

        }
        q.close();
        return listSG;

    }

    public static Dataset ReadDataset(String filename) {
        Dataset dataset = RDFDataMgr.loadDataset(filename);
        return dataset;
    }

    public static ArrayList<String> getEntities(Dataset dataset, String sg) {
        ArrayList<String> entities = new ArrayList<>();

        String queryString = String.format("SELECT DISTINCT ?s ?o\n"
                + "WHERE{\n"
                + "   	graph <%1$s>{\n"
                + "  			?s ?p ?o.\n"
                + "    		FILTER(!regex(str(?s),\"urn:graph:kws:[0-9]{3}:sol\"))\n"
                + "    		FILTER(!regex(str(?o),\"urn:graph:kws:[0-9]{3}:sol\"))\n"
                + "    		FILTER(!isLiteral(?o))\n"
                + "	}\n"
                + "}", sg);
        QueryExecution q = QueryExecutionFactory.create(queryString, dataset);
        ResultSet result = q.execSelect();
        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            String entity = String.valueOf(soln.get("s"));
            String entity_o = String.valueOf(soln.get("o"));
            entities.add(entity);
            entities.add(entity_o);
        }
        q.close();

        return entities;

    }

    public static HashMap<String, String> createRepresentationURI(Dataset dataset, String sg) {
        HashMap<String, String> mapURI = new HashMap<>();
        Integer count = 1;
        ArrayList<String> entities = getEntities(dataset, sg);

        for (String entity : entities) {
            if (!mapURI.containsKey(entity)) {
                String numberRepresentation = count.toString();
                mapURI.put(entity, numberRepresentation);
                count++;
            }
        }
        return mapURI;

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
                        System.out.println("TOTAL SUB GRAFOS DO SG: "+total_subgraphs);
                        total = total + total_subgraphs;
                    }
                    System.out.println(total);
                }
                count++;
            }
        }

    }
}
