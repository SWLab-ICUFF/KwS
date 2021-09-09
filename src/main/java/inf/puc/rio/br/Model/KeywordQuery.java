/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.Model;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.jena.rdf.model.Model;

/**
 *
 * @author angelo
 */
public class KeywordQuery {



    private ArrayList<Model> listModel;
    private ArrayList<Entity> allEntities;
    private String classURI;
    private String inducer;
    private HashMap<String, String> mapClassLabels;
    private HashMap<String, String> mapPropertyLabels;
    
    private ArrayList<Pattern> listPatterns;
    
    
    public KeywordQuery(){
        
    }

    /**
     * @return the listModel
     */
    public ArrayList<Model> getListModel() {
        return listModel;
    }

    /**
     * @param listModel the listModel to set
     */
    public void setListModel(ArrayList<Model> listModel) {
        this.listModel = listModel;
    }

    /**
     * @return the classURI
     */
    public String getClassURI() {
        return classURI;
    }

    /**
     * @param classURI the classURI to set
     */
    public void setClassURI(String classURI) {
        this.classURI = classURI;
    }

    /**
     * @return the inducer
     */
    public String getInducer() {
        return inducer;
    }

    /**
     * @param inducer the inducer to set
     */
    public void setInducer(String inducer) {
        this.inducer = inducer;
    }
    
    
    /**
     * @return the allEntities
    */
    public ArrayList<Entity> getAllEntities() {
        return allEntities;
    }

    /**
     * @param allEntities the allEntities to set
    */
    public void setAllEntities(ArrayList<Entity> allEntities) {
        this.allEntities = allEntities;
    }
    
    /**
     * @return the mapClassLabels
    */
    public HashMap<String, String> getMapClassLabels() {
        return mapClassLabels;
    }

    /**
     * @param mapClassLabels the mapClassLabels to set
    */
    public void setMapClassLabels(HashMap<String, String> mapClassLabels) {
        this.mapClassLabels = mapClassLabels;
    }
    
     /**
     * @return the mapPropertyLabels
    */
    public HashMap<String, String> getMapPropertyLabels() {
        return mapPropertyLabels;
    }

    /**
     * @param mapPropertyLabels the mapPropertyLabels to set
    */
    public void setMapPropertyLabels(HashMap<String, String> mapPropertyLabels) {
        this.mapPropertyLabels = mapPropertyLabels;
    }

    /**
     * @return the listPatterns
     */
    public ArrayList<Pattern> getListPatterns() {
        return listPatterns;
    }

    /**
     * @param listPatterns the listPatterns to set
     */
    public void setListPatterns(ArrayList<Pattern> listPatterns) {
        this.listPatterns = listPatterns;
    }
    
    
    
    
}