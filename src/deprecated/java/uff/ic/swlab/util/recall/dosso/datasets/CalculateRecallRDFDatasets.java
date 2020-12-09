/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uff.ic.swlab.util.recall.dosso.datasets;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.core.DatasetGraph;

/**
 *
 * @author angelo
 */
public class CalculateRecallRDFDatasets {
    
    private SPARQLMethods sparqlMethods;
    private HashMap<Integer, ArrayList<String>> mapReference;
    private HashMap<Integer, ArrayList<String>> mapAnswer;
    
    
    public CalculateRecallRDFDatasets(){
        mapReference = new HashMap<>();
        mapAnswer = new HashMap<>();
    }
    
    public void getReference(String nameDataset, String serviceDataset) {
        System.out.println("geting reference...");
        File folder = new File(String.format("./src/main/resources/benchmarks/Recall/queries_dataset/%1$s", nameDataset));
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        Integer count = 1;
        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println(i);
            File file = listOfFiles[i];
            ArrayList<String> listLabel = new ArrayList<>();
            try {
                String queryString = sparqlMethods.readQuery(file.toString());
                QueryExecution q = QueryExecutionFactory.sparqlService(serviceDataset, queryString);
                ResultSet result = q.execSelect();
                
                while(result.hasNext()){
                    
                    QuerySolution soln = result.nextSolution();
                    String label = String.valueOf(soln.get("label"));
                    listLabel.add(label);
                    
                }
                q.close();
                mapReference.put(count, listLabel);
                count++;
                
               
            } catch (IOException ex) {
                Logger.getLogger(CalculateRecallRDFDatasets.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
    }


    public void getAnswer(String nameDataset) throws IOException {
        System.out.println("geting answer");
        File folder = new File(String.format("./src/main/resources/benchmarks/Recall/queries_SG/%1$s", nameDataset));
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        Integer count = 1;
        for (int i = 0; i < listOfFiles.length; i++) {
            System.out.println(i);
            String pathSG = String.format("./src/main/resources/benchmarks/IS/%1$s/%2$03d.nq.gz", nameDataset, count);
        
            File file = listOfFiles[i];
            ArrayList<String> listLabel = new ArrayList<>();
            Dataset dataset = sparqlMethods.readDataset(pathSG);
            
            String queryString = sparqlMethods.readQuery(file.toString());
                
              
           QueryExecution q = QueryExecutionFactory.create(queryString, dataset);
           ResultSet result = q.execSelect();
           while(result.hasNext()){
               QuerySolution soln = result.nextSolution();
               String label = String.valueOf(soln.get("label"));
               listLabel.add(label);
           }
           dataset.close();
           mapAnswer.put(count, listLabel);
           count++;
        
         }
        
    }


    public HashMap<Integer, Double> calculateRecall() {
        System.out.println("Calculing recall..");
        HashMap<Integer, Double> mapRecall = new HashMap<>();
        
        for(Integer key: mapAnswer.keySet()){
            ArrayList<String> listAnswer = mapAnswer.get(key);
            ArrayList<String> listReference = mapReference.get(key);
            
            Double count_find = 0.0;
            for (String answer: listAnswer){
                if(listReference.contains(answer))
                    count_find++;
                
            }
            Double recall = count_find/listReference.size();
            mapRecall.put(key, recall);
            
            
        }
        return mapRecall;
        
        
    }

}
