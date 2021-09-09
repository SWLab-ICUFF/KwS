/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.Model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 *
 * @author angelo
 */
public class Entity {

    private String uriEntity;
    private String label;
    private HashMap<String, String> mapDataProperties;
    private Set<String> LabelProperties;
    
    
    public Entity(){
        
    }

    /**
     * @return the uriEntity
     */
    public String getUriEntity() {
        return uriEntity;
    }

    /**
     * @param uriEntity the uriEntity to set
     */
    public void setUriEntity(String uriEntity) {
        this.uriEntity = uriEntity;
    }

    /**
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label the label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return the mapDataProperties
     */
    public HashMap<String, String> getMapDataProperties() {
        return mapDataProperties;
    }

    /**
     * @param mapDataProperties the mapDataProperties to set
     */
    public void setMapDataProperties(HashMap<String, String> mapDataProperties) {
        this.mapDataProperties = mapDataProperties;
    }

    /**
     * @return the LabelProperties
     */
    public Set<String> getLabelProperties() {
        return LabelProperties;
    }

    /**
     * @param LabelProperties the LabelProperties to set
     */
    public void setLabelProperties(Set<String> LabelProperties) {
        this.LabelProperties = LabelProperties;
    }

    
    

    
    
    
    
}
