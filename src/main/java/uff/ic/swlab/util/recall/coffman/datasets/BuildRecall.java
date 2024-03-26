/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uff.ic.swlab.util.recall.coffman.datasets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.text.similarity.JaccardSimilarity;
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

    public static HashMap<Integer, ArrayList<String>> getAnswers(String nameDatabase) throws FileNotFoundException, IOException {
        HashMap<Integer, ArrayList<String>> mapAnswers = new HashMap<>();
        FileReader file = new FileReader(String.format("./src/main/resources/draft/Recall/Results/%1$s_search_id.csv", nameDatabase));
        try (BufferedReader br = new BufferedReader(file)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(";");
                Integer keyowrdNumber = Integer.parseInt(values[0]);
                ArrayList<String> listAnswers = new ArrayList<>();
                String[] answers = values[1].replace("[", "").replace("]", "").split(",");
                for (int i = 0; i < answers.length; i++)
                    listAnswers.add(answers[i]);
                mapAnswers.put(keyowrdNumber, listAnswers);

            }

        }

        return mapAnswers;

    }

    public static void ExportCSV(HashMap<Integer, Double> mapRecall, String nameService) throws FileNotFoundException {
        File folder = new File(String.format("./src/main/resources/draft/Recall/Results/%1$s_result.csv", nameService));
        try (PrintWriter writer = new PrintWriter(folder)) {
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

    public static Dataset readDataset(String filename) {

        Dataset dataset = RDFDataMgr.loadDataset(filename);
        return dataset;
    }

    public static void main(String[] args) throws FileNotFoundException, IOException {

        //entrada
        //Mondial
//        String nameDatabase = "Mondial";
//        Double similarityTrashold = 1.0;
        //IMDb
        String nameDatabase = "IMDb";
        Double similarityTrashold = 0.7;

        String queryString = "SELECT DISTINCT ?o\n"
                + "WHERE{\n"
                + "  graph ?sol{\n"
                + "  		?s ?p ?o.\n"
                + "    FILTER(isLiteral(?o))\n"
                + "  }\n"
                + "}";

        HashMap<Integer, ArrayList<String>> mapAnswers = getAnswers(nameDatabase);

        HashMap<Integer, Double> mapRecall = new HashMap<>();
        Integer count = 0;
        for (Integer keywordNumber : mapAnswers.keySet()) {
            System.out.println("Calculing recall " + keywordNumber);
            ArrayList<String> listAnswer = mapAnswers.get(keywordNumber);
            Integer allAnswers = listAnswer.size();
            String pathSG = String.format("./src/main/resources/benchmarks/ESWC2021/%1$s/%2$03d.nq.gz", nameDatabase, keywordNumber);
            Double countAnswers = 0.0;
            Dataset dataset = readDataset(pathSG);
            for (String answerReference : listAnswer) {
                answerReference = answerReference.toLowerCase().replace(" ", "");
                QueryExecution q = QueryExecutionFactory.create(queryString, dataset);
                ResultSet result = q.execSelect();
                while (result.hasNext()) {
                    QuerySolution soln = result.nextSolution();
                    String name = String.valueOf(soln.get("o")).toLowerCase().replace(" ", "");
                    double similarity = new JaccardSimilarity().apply(name, answerReference);
                    if (similarity >= similarityTrashold) {
                        countAnswers++;
                        break;
                    }
                }
                q.close();
            }
            double recall = countAnswers / allAnswers;
            System.out.println(recall);
            mapRecall.put(keywordNumber, recall);

        }

        ExportCSV(mapRecall, nameDatabase);

    }

}
