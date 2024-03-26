/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.Model;

import java.util.ArrayList;
import java.util.HashMap;
import util.Graph;

/**
 *
 * @author angelo
 */
public class GraphObject {

    private Graph graph;
    private HashMap<ArrayList<String>, String> representationEdges;
    private HashMap<ArrayList<String>, String> repeatedRelations;

    public GraphObject() {

    }

    /**
     * @return the graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * @param graph the graph to set
     */
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    /**
     * @return the representationEdges
     */
    public HashMap<ArrayList<String>, String> getRepresentationEdges() {
        return representationEdges;
    }

    /**
     * @param representationEdges the representationEdges to set
     */
    public void setRepresentationEdges(HashMap<ArrayList<String>, String> representationEdges) {
        this.representationEdges = representationEdges;
    }

    /**
     * @return the repeatedRelations
     */
    public HashMap<ArrayList<String>, String> getRepeatedRelations() {
        return repeatedRelations;
    }

    /**
     * @param repeatedRelations the repeatedRelations to set
     */
    public void setRepeatedRelations(HashMap<ArrayList<String>, String> repeatedRelations) {
        this.repeatedRelations = repeatedRelations;
    }

}
