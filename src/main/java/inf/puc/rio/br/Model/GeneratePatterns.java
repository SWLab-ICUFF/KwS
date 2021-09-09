/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.Model;

import java.util.ArrayList;
import java.util.HashMap;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 *
 * @author angelo
 */
public class GeneratePatterns {
    

    private Model model;
    private String keyword;
    private HashMap<String, String> keywordsSelected;
    
    public GeneratePatterns(Model model, String keyword, HashMap<String, String> keywordsSelected){
        this.model = model;
        this.keyword = keyword;
        this.keywordsSelected = keywordsSelected;
    
    }
    
    public String ConstructPatternsLabels(){
        
        ArrayList<String> allPatterns = new ArrayList<>();
        StmtIterator iter = model.listStatements();
        
        ArrayList<String> listKeywordsPredicate = new ArrayList<>();
        String query = "SELECT ?label WHERE {?p <http://www.w3.org/2000/01/rdf-schema#label> ?label. FILTER EXISTS{?s ?p ?o.}}";
        QueryExecution q = QueryExecutionFactory.create(query, model);
        ResultSet result = q.execSelect();
        while(result.hasNext()){
            QuerySolution soln = result.nextSolution();
            String label = soln.get("label").toString();
            listKeywordsPredicate.add(label);
        }
        
        while (iter.hasNext()) {
            String pattern = "";
            Statement stmt = iter.nextStatement();
            
            String subject = stmt.getSubject().toString();
            String predicate = stmt.getPredicate().toString();
            RDFNode object = stmt.getObject();
            
            if (object instanceof Resource) {
                pattern = "<"+subject + "> <" + predicate + "> <" + object.toString() + ">.";
                allPatterns.add(pattern);
            }else if(predicate.equals("http://www.w3.org/2000/01/rdf-schema#label") && !listKeywordsPredicate.contains(object.toString())){
                String keywords = keywordsSelected.get(object.toString());
                if(keywords != null)
                    pattern = String.format("<"+subject + "> <" + predicate + "> regex('%s').", keywords);
                    allPatterns.add(pattern);
            }
            
        }
       
        return String.join(" ", allPatterns);
        
    
    }
    
    
    
    
    
}
