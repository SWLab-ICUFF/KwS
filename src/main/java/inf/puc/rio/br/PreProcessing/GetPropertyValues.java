/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.PreProcessing;

import inf.puc.rio.br.Model.Constants;
import inf.puc.rio.br.Model.Entity;
import inf.puc.rio.br.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;

/**
 *
 * @author angelo
 */
public class GetPropertyValues {

    public static ArrayList<Entity> getPropertyValues(ArrayList<String> entityNeighborhood) throws IOException {
        ArrayList<Entity> allEntities = new ArrayList<>();
        
        
        
//        String queryString = Utils.readQuery(String.format("%1$s/get_entities.rq", Constants.pathSparqlPreProcessing));
//        QueryExecution q = QueryExecutionFactory.sparqlService(Constants.serviceDatabase, queryString);
//        ResultSet result = q.execSelect();
        
        for(String entityURI: entityNeighborhood){
        
     
            Entity entity = new Entity();
            
            
            HashMap<String, String> mapPropertyValues = new HashMap<>();
            
            String queryStringPropertyValues = Utils.readQuery(String.format("%1$s/get_PropertyValues.rq", Constants.pathSparqlPreProcessing));
            queryStringPropertyValues = String.format(queryStringPropertyValues, entityURI);

            QueryExecution q = QueryExecutionFactory.sparqlService(Constants.serviceDatabase, queryStringPropertyValues);
            ResultSet result = q.execSelect();
            String label = null;
            Set<String> Setlabelpredicate = new HashSet<>();
            while (result.hasNext()) {
                QuerySolution soln = result.nextSolution();
                
                String predicate = soln.get("predicate").toString();
                Literal lit = soln.getLiteral("literal");
                label = soln.get("label").toString();
                String labelPredicate = soln.getLiteral("labelPredicate").toString();
                
                Setlabelpredicate.add(labelPredicate);
                
                String valueLit = lit.getValue().toString();
                mapPropertyValues.put(predicate, valueLit);
                
            }
            q.close();
            entity.setLabelProperties(Setlabelpredicate);
            entity.setUriEntity(entityURI);
            entity.setLabel(label);
            entity.setMapDataProperties(mapPropertyValues);
           
            allEntities.add(entity);

        }
   

        return allEntities;
    }

}
