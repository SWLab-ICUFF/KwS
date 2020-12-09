/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uff.ic.swlab.util.recall.dosso.datasets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.RDFDataMgr;
/**
 *
 * @author angelo
 */
public class BuildRecall {

    public static Dataset ReadDataset(String filename) {
        Dataset dataset = RDFDataMgr.loadDataset(filename);
        return dataset;

    }

    public static String readQuery(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        byte[] data = new byte[(int) file.length()];
        try ( FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }
        return new String(data, "UTF-8");
    }

    public static boolean executeTriplePattern(Dataset dataset, String triplePattern) {
        String queryString = String.format("SELECT ?sol WHERE{graph ?sol{%1$s}}", triplePattern);

        QueryExecution q = QueryExecutionFactory.create(queryString, dataset);
        ResultSet result = q.execSelect();
        while (result.hasNext()) {
            //QuerySolution soln = result.nextSolution();
            return true;
        }

        return false;

    }

    public static void ExportCSV(HashMap<Integer, Double> mapRecall, String nameService) throws FileNotFoundException {
        File folder = new File(String.format("./src/main/resources/benchmarks/Recall/Results/%1$s_result.csv", nameService));
        try ( PrintWriter writer = new PrintWriter(folder)) {
            StringBuilder sb = new StringBuilder();
            sb.append("query");
            sb.append(',');
            sb.append("Recall");
            sb.append('\n');

            for (Integer key : mapRecall.keySet()) {
                Double recall = mapRecall.get(key);

                sb.append(key);
                sb.append(',');
                sb.append(recall);
                sb.append('\n');
            }

            writer.write(sb.toString());

        }

    }
    
    public static HashMap<Integer, Double> calculateRecallSyntecticDatasets(String nameDataset, String serviceDatabase) throws IOException{
        HashMap<Integer, Double> mapRecall = new HashMap<>();

        File folder = new File(String.format("./src/main/resources/benchmarks/IS/%1$s", nameDataset));
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        Integer count = 1;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].toString().endsWith(".nq.gz")) {
                System.out.println(i);
                //read S.G
                Dataset dataset = ReadDataset(listOfFiles[i].toString());

                String pathQuery = String.format("./src/main/resources/benchmarks/Recall/queries_dataset/%1$s/%2$03d.rq", nameDataset, count);
                String queryString = readQuery(pathQuery);
                QueryExecution q = QueryExecutionFactory.sparqlService(serviceDatabase, queryString);
                ResultSet result = q.execSelect();
                Double allAnsewrs = 0.0;
                Integer ansewrsFind = 0;

                while (result.hasNext()) {
                    QuerySolution soln = result.nextSolution();
                    Iterator<String> iteratorVars = soln.varNames();
                    String allTriplepatterns = "";
                    Integer answer_flag = 0;
                    while (iteratorVars.hasNext()) {

                        String triplePattern = String.valueOf(soln.get(iteratorVars.next()));
                        if (!executeTriplePattern(dataset, triplePattern)) {
                            answer_flag = 1;

                            break;
                        }

                    }
                    if (answer_flag == 0) {
                        ansewrsFind++;
                    }

                    allAnsewrs++;
                }
                double recall = ansewrsFind / allAnsewrs;
                mapRecall.put(count, recall);

                count++;
                q.close();
                dataset.close();
                System.gc();

            }
        }
        return mapRecall;
        
    }
    
    
    public static HashMap<Integer, Double> calculateRecallDBPedia(String nameDataset, String serviceDatabase) throws IOException{
        HashMap<Integer, Double> mapRecall = new HashMap<>();

        File folder = new File(String.format("./src/main/resources/benchmarks/IS/%1$s", nameDataset));
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);
        Integer count = 1;
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].toString().endsWith(".nq.gz")) {
                System.out.println(i);
                //read S.G
                Dataset dataset = ReadDataset(listOfFiles[i].toString());

                String pathQuery = String.format("./src/main/resources/benchmarks/Recall/queries_dataset/%1$s/%2$03d.rq", nameDataset, count);
                String queryString = readQuery(pathQuery);
                QueryExecution q = QueryExecutionFactory.sparqlService(serviceDatabase, queryString);
                ResultSet result = q.execSelect();
                Double allAnsewrs = 0.0;
                Integer ansewrsFind = 0;

                while (result.hasNext()) {
                    QuerySolution soln = result.nextSolution();
                    Iterator<String> iteratorVars = soln.varNames();
                    String allTriplepatterns = "";
                    Integer answer_flag = 0;
                    while (iteratorVars.hasNext()) {

                        String triplePattern = String.valueOf(soln.get(iteratorVars.next()));
                        if (executeTriplePattern(dataset, triplePattern)) {
                             ansewrsFind++;
                        }

                    allAnsewrs++;
                    }
                   

                   
                }
                double recall = ansewrsFind / allAnsewrs;
                mapRecall.put(count, recall);
   
                count++;
                q.close();
                dataset.close();
                System.gc();

            }
        }
        return mapRecall;
        
    }


    public static void main(String[] args) throws IOException {
        String nameDataset = "DBPedia_70M";
        String serviceDatabase = String.format("http://semanticweb.inf.puc-rio.br:3030/%1$s/sparql", nameDataset);


        //HashMap<Integer, Double> mapRecall = calculateRecallSyntecticDatasets(nameDataset, serviceDatabase);
        HashMap<Integer, Double> mapRecall = calculateRecallDBPedia(nameDataset, serviceDatabase);
        ExportCSV(mapRecall, nameDataset);
    }

}
