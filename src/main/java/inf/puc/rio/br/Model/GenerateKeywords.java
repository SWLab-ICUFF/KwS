/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.Model;

import inf.puc.rio.br.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author angelo
 */

public class GenerateKeywords {
   
    private KeywordQuery kw;
    
    private ArrayList<String> entitiesST;
    private ArrayList<String> classesST;
    private ArrayList<String> propertiesST;
    
    private HashMap<Entity, String> mapKeywordsEntity;
    private HashMap<String, String> mapKeywordsClass;
    private HashMap<String, String> mapKeywordsProperties;
    
    private HashMap<String, String> keywordSelected;
   
  
    public GenerateKeywords(KeywordQuery kw,
            ArrayList<String> entitiesST,
            ArrayList<String> classesST,
            ArrayList<String> propertiesST) throws IOException{
       
        this.kw = kw;
        this.entitiesST = entitiesST;
        this.classesST = classesST;
        this.propertiesST = propertiesST;
        
        SelectKeywords();
    }
    
    public void SelectKeywords() throws IOException{
        
        HashMap<String, String> mapClassLabels = kw.getMapClassLabels();
        mapKeywordsClass = new HashMap<>();
        mapKeywordsProperties = new HashMap<>();
        mapKeywordsEntity = new HashMap<>();
        
        for(String classesURI: classesST){
            String keywords = mapClassLabels.get(classesURI);
            mapKeywordsClass.put(keywords, keywords);
            
        }
        
        HashMap<String, String> mapPropertyLabels = kw.getMapPropertyLabels();
        
        for(String propertiesURI: propertiesST){
            String keywords = mapPropertyLabels.get(propertiesURI);
            mapKeywordsProperties.put(keywords, keywords);
        }
        
       
        for(String entityURI: entitiesST){
            Entity entityInstance = Utils.findEntity(kw.getAllEntities(), entityURI);
            if(entityInstance != null)
                mapKeywordsEntity.put(entityInstance, entityInstance.getLabel());
            
        }
         
       
        
    }
    
    public String generateKeywordClass(){
        ArrayList<String> keywordsList = new ArrayList<>();
        
         for (Map.Entry<String, String> entry : mapKeywordsClass.entrySet()) {
            String words = entry.getValue();
            keywordsList.add(words);
        }
        return String.join(" ", keywordsList);
        
    }
    
    public String generateKeywordObjectProperties(){
        ArrayList<String> keywordsList = new ArrayList<>();
        for (Map.Entry<String, String> entry : mapKeywordsProperties.entrySet()) {
             String words = entry.getValue();
             keywordsList.add(words);
        }
        return String.join(" ", keywordsList);
        
    }
    
    public String generateKeywordEntity(){
        ArrayList<String> keywordsListEntity = new ArrayList<>();
        Set<String> keywordLabelProperties = new HashSet<>();
        ArrayList<String> keywordValueProperties = new ArrayList<>();
        

        for (Map.Entry<Entity, String> entry : mapKeywordsEntity.entrySet()) {
            
            Entity entity = entry.getKey();
            keywordsListEntity.add(entity.getLabel());
            
            HashMap<String, String> mapPropertyValues = entity.getMapDataProperties();
            
            for (Map.Entry<String, String> entryPropertyValues : mapPropertyValues.entrySet()) {
                 keywordValueProperties.add(entryPropertyValues.getValue());
            }
            
            keywordLabelProperties = entity.getLabelProperties();
            
        }
        
        String allentityWords = String.join(" ", keywordsListEntity).replace("null", "");
        String allLabelPropertiesWords = String.join(" ", keywordLabelProperties);
        String allkeywordValuePropertiesWords = String.join(" ", keywordValueProperties);
        
        return allentityWords + " " + allLabelPropertiesWords + " " + allkeywordValuePropertiesWords;
        
    }

   


        
    
    
}
