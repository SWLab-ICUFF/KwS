/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.Main;

import inf.puc.rio.br.Model.Constants;
import inf.puc.rio.br.Model.Entity;
import inf.puc.rio.br.Model.GenerateKeywords;
import inf.puc.rio.br.Model.KeywordQuery;
import inf.puc.rio.br.Model.Pattern;
import inf.puc.rio.br.server.FusekiServer;
import inf.puc.rio.br.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

/**
 *
 * @author angelo
 */
public class GenerateKeywordQuery {

    public static ArrayList<Pattern> generate(FusekiServer fuseki, KeywordQuery kw) throws IOException {

        String queryString = null;

        System.out.println("Step 5 Instance STs");
        queryString = Utils.readQuery(String.format("%1$s/insert_class_neighborhood.rq", Constants.pathSparlInstance));
        queryString = String.format(queryString, Constants.serviceDatabase);
        fuseki.execUpdate(queryString, "KwS.temp");

        Model Neighborhood = RDFConnectionFactory.connectFuseki(Constants.serviceKwSTempData).fetch("urn:graph:kws:inducer:Neighborhood");

        ArrayList<Pattern> listPattern = new ArrayList<>();
        ArrayList<Model> listSTs = kw.getListModel();
        
        ArrayList<String> allPatterns = new ArrayList<>();
        for (Model modelST : listSTs) {

            StmtIterator modelIterator = modelST.listStatements();

            int instanceSubjectNumber = 1;
            int instanceObjectNumber = 1;

            HashMap<String, String> mapInstance = new HashMap<>();
            ArrayList<String> patternList = new ArrayList<>();
            Set<String> variablesSet = new HashSet<>();
            ArrayList<String> predicatesList = new ArrayList<>();
            ArrayList<String> classesURI = new ArrayList<>();
            if (modelST.size() == 1) {
                while (modelIterator.hasNext()) {
                    Statement stmt = modelIterator.nextStatement();

                    String classObject = stmt.getObject().toString();
                    if (classObject.contains("_Mirror_")) {
                        classObject = classObject.replaceAll("_Mirror_[0-9]", "");
                    }
                    String pattern = "?instanceSubjectNumber" + instanceSubjectNumber + " a " + "<" + classObject + ">.";
                    patternList.add(pattern);
                    classesURI.add(classObject);

                }
                variablesSet.add("?instanceSubjectNumber" + instanceSubjectNumber);
                
            } else if (modelST.size() <= 2) {
                while (modelIterator.hasNext()) {

                    Statement stmt = modelIterator.nextStatement();

                    String classSubject = stmt.getSubject().toString();
                    String classObject = stmt.getObject().toString();

                    if (!mapInstance.containsKey(classSubject)) {
                        mapInstance.put(classSubject, "?instanceSubjectNumber" + instanceSubjectNumber);
                        instanceSubjectNumber++;
                    }

                    if (!mapInstance.containsKey(classObject)) {
                        mapInstance.put(classObject, "?instanceObjectNumber" + instanceObjectNumber);
                        instanceObjectNumber++;
                    }

                }

                StmtIterator newmodelIterator = modelST.listStatements();

                while (newmodelIterator.hasNext()) {
                    Statement stmt = newmodelIterator.nextStatement();

                    String classSubject = stmt.getSubject().toString();
                    String classObject = stmt.getObject().toString();
                    String uriRelation = stmt.getPredicate().toString();

                    String pattern = mapInstance.get(classSubject) + " <" + uriRelation + "> " + mapInstance.get(classObject) + ".";
                    patternList.add(pattern);
                    predicatesList.add(uriRelation);

                }
                //construindo o set de variables
                for (Map.Entry<String, String> entry : mapInstance.entrySet()) {
                    variablesSet.add(entry.getValue());
                }

            }

            String variables = String.join(" ", variablesSet);
            String queryPatternInstance = String.join("\n", patternList);

            if (patternList.size() > 0 && !allPatterns.contains(queryPatternInstance)) {
                allPatterns.add(queryPatternInstance);
                
                String querygetInstance = Utils.readQuery(String.format("%1$s/get_instance.rq", Constants.pathSparlInstance));
                querygetInstance = String.format(querygetInstance, queryPatternInstance, variables);
                QueryExecution qInstance = QueryExecutionFactory.create(querygetInstance, Neighborhood);
                ResultSet rInstance = qInstance.execSelect();

                //construindo patten da  declaracao de classes
                //ArrayList<String> classesURI = new ArrayList<>();
                for (Map.Entry<String, String> entry : mapInstance.entrySet()) {
                    String className = entry.getKey();
                    //revisar
                    if (className.contains("_Mirror_")) {
                        className = className.replaceAll("_Mirror_[0-9]", "");

                    }
                    classesURI.add(className);
                    String pattern = entry.getValue() + " a " + "<" + className + ">.";
                    patternList.add(pattern);

                }

                String queryPattern = String.join("\n", patternList);

                //para cada pattern extrair as keywords
                Pattern pattern = new Pattern();
                pattern.setPattern(queryPattern);
                System.out.println("PATTERN: " + queryPattern);
                System.out.println("Step 6... Extract kwywords from instances...");

                Set<String> keywordLabelsList = new HashSet<>();
                while (rInstance.hasNext()) {
                    //cada linha eh uma instancia
                    QuerySolution soln = rInstance.nextSolution();

                    Iterator<String> iteratorVariables = soln.varNames();

                    ArrayList<String> listURIInstace = new ArrayList<>();
                    while (iteratorVariables.hasNext()) {
                        String var = iteratorVariables.next();

                        String uriInstance = soln.get(var).toString();
                        listURIInstace.add(uriInstance);
                    }
                    
                    GenerateKeywords generate = new GenerateKeywords(kw, listURIInstace, classesURI, predicatesList);
                    
                    String keywordClass = generate.generateKeywordClass();
                    String propertyLabels = generate.generateKeywordObjectProperties();
                    String entities = generate.generateKeywordEntity();
              
                    
                    String keywordsLabels = keywordClass + " " + propertyLabels + " " + entities;
                    
                    if(!keywordLabelsList.contains(keywordsLabels)){
                        keywordLabelsList.add(keywordsLabels);
                        System.out.println(keywordsLabels);
                    }
                    


                }
                
                if(keywordLabelsList.size() > 0){
                    pattern.setKeywordLabel(keywordLabelsList);

                    listPattern.add(pattern);
                }

            }

        }
        return listPattern;

    }

    public static ArrayList<Entity> getEntitiesFromModel(Model model, ArrayList<Entity> allEntities) {
        ArrayList<Entity> entitiyList = new ArrayList<>();

        StmtIterator iter = model.listStatements();
        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            RDFNode object = stmt.getObject();

            for (Entity entity : allEntities) {
                if (subject.getURI().equals(entity.getUriEntity()) && !entitiyList.contains(entity)) {
                    entitiyList.add(entity);
                    break;
                }
            }

            if (object instanceof Resource) {
                for (Entity entity : allEntities) {
                    if (object.toString().equals(entity.getUriEntity()) && !entitiyList.contains(entity)) {
                        entitiyList.add(entity);
                        break;
                    }

                }
            }

        }

        return entitiyList;

    }

}