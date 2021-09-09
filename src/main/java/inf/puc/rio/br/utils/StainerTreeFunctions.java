/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.utils;

import inf.puc.rio.br.Model.GraphObject;
import inf.puc.rio.br.server.FusekiServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mitre.Mitre;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import util.Graph;

/**
 *
 * @author angelo
 */


public class StainerTreeFunctions {

    public static ArrayList<Model> createSTOnlyNode(Set<Set<String>> powerSet, ArrayList<Model> modelSts) {

        for (Set<String> set : powerSet) {
            ArrayList<String> listTriple = new ArrayList<>();
            if (set.size() == 1) {
                String vURI = "urn:graph:kws:inducer:Subject";
                String predicate = "urn:graph:kws:inducer:predicate";
                String triple = "";

                for (String element : set) {
                    triple = vURI + " " + predicate + " " + element + "";
                }

                listTriple.add(triple);
                Model modelST = Utils.createModelFromTriples(listTriple);
                modelSts.add(modelST);
            }
        }

        return modelSts;

    }

    public static HashMap<String, ArrayList<ArrayList<String>>> createRepresentationTriple(Model model) throws IOException {
        HashMap<String, ArrayList<ArrayList<String>>> representationURI = new HashMap<>();

        String queryString = "SELECT ?s ?p ?o WHERE {?s ?p ?o.}";
        QueryExecution q = QueryExecutionFactory.create(queryString, model);
        ResultSet result = q.execSelect();

        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            ArrayList<String> listNode = new ArrayList<>();

            String predicateURI = soln.get("p").toString();
            String subjectURI = soln.get("s").toString();
            String objectURI = soln.get("o").toString();

            listNode.add(subjectURI);
            listNode.add(objectURI);
            ArrayList<ArrayList<String>> listNodesSubjectandObject = new ArrayList<>();
            listNodesSubjectandObject.add(listNode);
            if (representationURI.containsKey(predicateURI)) {
                ArrayList<ArrayList<String>> listAllNodes = representationURI.get(predicateURI);
                listAllNodes.add(listNode);
            } else {
                representationURI.put(predicateURI, listNodesSubjectandObject);
            }

        }

        return representationURI;

    }

    public static GraphObject createGraph(Model model, HashMap<String, Integer> representationNode) {

        Graph graph = new Graph();
        Map<String, String> edges = new HashMap<>();

        String queryString = "SELECT ?s ?p ?o WHERE {?s ?p ?o.}";
        QueryExecution q = QueryExecutionFactory.create(queryString, model);
        ResultSet result = q.execSelect();

        HashMap<ArrayList<String>, String> representationEdges = new HashMap<>();
        HashMap<ArrayList<String>, String> repeatedRelations = new HashMap<>();

        GraphObject graphObject = new GraphObject();

        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();

            String s = String.valueOf(soln.get("s"));
            String o = String.valueOf(soln.get("o"));
            String p = String.valueOf(soln.get("p"));

            if (!(edges.containsKey(s) && edges.get(s).equals(o))
                    && !(edges.containsKey(o) && edges.get(o).equals(s))) {

                int numberRepresentationS = representationNode.get(s);
                int numberRepresentationO = representationNode.get(o);

                graph.addEdge(String.valueOf(numberRepresentationS), String.valueOf(numberRepresentationO));

                edges.put(s, o);

                ArrayList<String> nodes = new ArrayList<>();
                nodes.add(s);
                nodes.add(o);
                representationEdges.put(nodes, p);

            } else {
                ArrayList<String> nodes = new ArrayList<>();
                nodes.add(s);
                nodes.add(o);
                repeatedRelations.put(nodes, p);

            }
        }
        graphObject.setGraph(graph);
        graphObject.setRepeatedRelations(repeatedRelations);
        graphObject.setRepresentationEdges(representationEdges);

        return graphObject;
    }

    public static HashMap<String, Integer> createRepresentationURI(Set<String> nodeURIlist) {
        HashMap<String, Integer> mapRepresentation = new HashMap<>();
        Integer representationNumber = 0;
        for (String nodeURI : nodeURIlist) {
            representationNumber++;
            mapRepresentation.put(nodeURI, representationNumber);
        }

        return mapRepresentation;
    }

    public static ArrayList<Model> GenerateSTs(Set<Set<String>> powerSet, Model schemaModel, FusekiServer fuseki) throws IOException {

        ArrayList<Model> listSTs = new ArrayList<>();
        Set<String> nodeURIlist = new HashSet<>();

        StmtIterator modelIterator = schemaModel.listStatements();

        while (modelIterator.hasNext()) {
            Statement stmt = modelIterator.nextStatement();

            String subject = stmt.getSubject().toString();
            String object = stmt.getObject().toString();

            nodeURIlist.add(subject);
            nodeURIlist.add(object);
        }

        HashMap<String, Integer> representationNode = StainerTreeFunctions.createRepresentationURI(nodeURIlist);

        GraphObject graphObject = StainerTreeFunctions.createGraph(schemaModel, representationNode);
        HashMap<ArrayList<String>, String> repeatedRelations = graphObject.getRepeatedRelations();

        Integer count = 1;
        Integer countSet = 1;
        for (Set<String> set : powerSet) {
//            if (set.contains("http://www.semwebtech.org/mondial/10/meta#Country") && set.contains("http://www.semwebtech.org/mondial/10/meta#Organization") && set.contains("http://www.semwebtech.org/mondial/10/meta#Country_Mirror_0")
//                    && set.size() == 3) {
//                System.out.println("oi");
//            }
            if (set.size() > 1) {

                ArrayList<String> nodeTerminallist = new ArrayList<>();

                for (String element : set) {
                    String terminal = representationNode.get(element).toString();
                    nodeTerminallist.add(terminal);

                }
                try {
                    List<Graph> steinerTrees = Mitre.execute(graphObject.getGraph(), nodeTerminallist);
                    //System.out.println("Generating ST...." + countSet);
                    countSet++;
                    for (Graph tree : steinerTrees) {

                        //System.out.println(tree);

                        ArrayList<String> listTriple = transformGraph(tree, representationNode, graphObject);
                        Model modelST = Utils.createModelFromTriples(listTriple);
                        //Utils.printTriples(modelST);
                        listSTs.add(modelST);

                        //repeated relations
                        for (Map.Entry<ArrayList<String>, String> entry : repeatedRelations.entrySet()) {
                            Model model = ModelFactory.createDefaultModel();

                            ArrayList<String> nodes = entry.getKey();
                            String relation = entry.getValue();

                            String s = nodes.get(0);
                            String o = nodes.get(0);

                            Property p = model.createProperty(relation);
                            Resource resource = model.createResource(s);

                            Node node = NodeFactory.createURI(o);
                            RDFNode nodeRDF = model.asRDFNode(node);

                            model.add(resource, p, nodeRDF);

                            StmtIterator iter = modelST.listStatements();

                            while (iter.hasNext()) {
                                Statement stmt = iter.nextStatement();

                                String subject = stmt.getSubject().toString();
                                String object = stmt.getObject().toString();

                                if (!subject.equals(s) && !object.equals(o)) 
                                    model.add(stmt);
                                

                            }
                            //listSTs.add(model);
                            //Utils.printTriples(model);

                        }

                    }
                } catch (Throwable e) {
                    continue;
                }

            }

        }

        return listSTs;
    }

    public static ArrayList<String> transformGraph(Graph tree, HashMap<String, Integer> representationNode, GraphObject graphObject) {

        ArrayList<String> listTriple = new ArrayList<>();

        HashMap<ArrayList<String>, String> mapRelations = graphObject.getRepresentationEdges();
        ArrayList<ArrayList<Integer>> nodesRelation = new ArrayList<>();

        ArrayList<String> verticeList = tree.verticesList();
        for (String v : verticeList) {
            Iterable<String> iterJ = tree.adjacentTo(v);

            iterJ.forEach((j) -> {

                int nodeV = Integer.parseInt(v);
                int nodeJ = Integer.parseInt(j);

                String vURI = Utils.getKeyByValue(representationNode, nodeV);
                String jURI = Utils.getKeyByValue(representationNode, nodeJ);

                String triple = null;
                for (Map.Entry<ArrayList<String>, String> entry : mapRelations.entrySet()) {
                    ArrayList<String> nodes = entry.getKey();
                    String s = nodes.get(0);
                    String o = nodes.get(1);

                    if (s.equals(vURI) && o.equals(jURI)) {

                        boolean result = Utils.findNode(nodesRelation, nodeV, nodeJ);

                        if (!result) {
                            ArrayList<Integer> relation = new ArrayList<>();
                            relation.add(nodeV);
                            relation.add(nodeJ);

                            ArrayList<Integer> relationInverted = new ArrayList<>();
                            relationInverted.add(nodeJ);
                            relationInverted.add(nodeV);

                            nodesRelation.add(relationInverted);
                            nodesRelation.add(relation);

                            triple = vURI + " " + entry.getValue() + " " + jURI + "";
                            listTriple.add(triple);

                        }

                    }

                }

            });
        }

        return listTriple;

    }

}
