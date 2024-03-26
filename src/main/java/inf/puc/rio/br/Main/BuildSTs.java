package inf.puc.rio.br.Main;

import inf.puc.rio.br.Model.Constants;
import inf.puc.rio.br.Model.Entity;
import inf.puc.rio.br.Model.KeywordQuery;
import inf.puc.rio.br.Model.Pattern;
import inf.puc.rio.br.PreProcessing.GetPropertyValues;
import inf.puc.rio.br.server.FusekiServer;
import inf.puc.rio.br.utils.StainerTreeFunctions;
import inf.puc.rio.br.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;


/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author angelo
 */
public class BuildSTs {

    public static void main(String[] args) throws IOException {

        FusekiServer fuseki = new FusekiServer("semanticweb.inf.puc-rio.br", 3030);
        String queryString = null;

        System.out.println("Pre processing...");

        System.out.println("Get labels Class");
        HashMap<String, String> mapClassLabels = Utils.getClassLabels();

        System.out.println("Get labels Properties");
        HashMap<String, String> mapPropertiesLabels = Utils.getPropertiesLabels();

        System.out.println("The end of pre processing...");

        queryString = Utils.readQuery(String.format("%1$s/drop_all.rq", Constants.pathSparlPreparation));
        fuseki.execUpdate(queryString, "KwS.temp");

        System.out.println("Step 1: Select Inducers");
        ArrayList<String> listClass = new ArrayList<>();

        queryString = Utils.readQuery(String.format("%1$s/select_class.rq", Constants.pathSparlInducers));
        QueryExecution q = QueryExecutionFactory.sparqlService(Constants.serviceDatabase, queryString);
        ResultSet result = q.execSelect();

        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();
            listClass.add(soln.get("class").toString());
        }
        q.close();

        ArrayList<KeywordQuery> listkw = new ArrayList<>();

        for (String classURI : listClass) {

            KeywordQuery kw = new KeywordQuery();

            kw.setMapClassLabels(mapClassLabels);
            kw.setMapPropertyLabels(mapPropertiesLabels);

            kw.setClassURI(classURI);

            queryString = Utils.readQuery(String.format("%1$s/drop_all.rq", Constants.pathSparlPreparation));
            fuseki.execUpdate(queryString, "KwS.temp");

            queryString = Utils.readQuery(String.format("%1$s/select_inducers.rq", Constants.pathSparlInducers));
            queryString = String.format(queryString, Constants.serviceDatabase, classURI);
            fuseki.execUpdate(queryString, "KwS.temp");

            System.out.println("Step 2: Compute Neighborhood for inducer of class " + classURI);
            queryString = Utils.readQuery(String.format("%1$s/compute_neighborhood.rq", Constants.pathSparlNeighborhood));
            queryString = String.format(queryString, Constants.serviceDatabase);
            fuseki.execUpdate(queryString, "KwS.temp");

            ArrayList<String> entityNeighborhood = Utils.getSetNodesNeighborhood();

            ArrayList<Entity> allEntities = GetPropertyValues.getPropertyValues(entityNeighborhood);
            kw.setAllEntities(allEntities);

            System.out.println("Step 3: Cut Out Schema for inducer of class " + classURI);

            queryString = Utils.readQuery(String.format("%1$s/cut_out_schema.rq", Constants.createSchema));
            queryString = String.format(queryString, Constants.serviceDatabase);
            fuseki.execUpdate(queryString, "KwS.temp");

            System.out.println("Step 3.1: Creating Mirrors of Cut out Schema for inducer of class " + classURI);
            Model schemaModel = Utils.createMirrorFromGraph(1, Constants.serviceKwSTempData, "urn:graph:kws:CutOutSchema");

            System.out.println("Step 3.2: Creating Power Set of inducer of class " + classURI);
            Set<String> elements = Utils.getElementsFromModel(schemaModel);
            Set<Set<String>> powerSet = Utils.powerSet(elements, 2);

            System.out.println("Step 3.3: Computing heuristics... " + classURI);

            Set<Set<String>> powerSetFilterClass = Utils.filterPowerSetbyClass(powerSet, classURI);
            Set<Set<String>> powerSetFilterPairsMirror = Utils.filterPairMirrorPowerSet(powerSetFilterClass);

            System.out.println("Step 4: Generate STs for inducer of class " + classURI);
            System.out.println("Size of sets: " + powerSetFilterPairsMirror.size());

            ArrayList<Model> modelSts = StainerTreeFunctions.GenerateSTs(powerSetFilterPairsMirror, schemaModel, fuseki);
            modelSts = StainerTreeFunctions.createSTOnlyNode(powerSetFilterPairsMirror, modelSts);

            kw.setListModel(modelSts);

            ArrayList<Pattern> patternList = GenerateKeywordQuery.generate(fuseki, kw);
            kw.setListPatterns(patternList);

            listkw.add(kw);

            queryString = Utils.readQuery(String.format("%1$s/drop_all.rq", Constants.pathSparlPreparation));
            fuseki.execUpdate(queryString, "KwS.temp");

        }

        Utils.ExportResults(listkw);
    }

}
