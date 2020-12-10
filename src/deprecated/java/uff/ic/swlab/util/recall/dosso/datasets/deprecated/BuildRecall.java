/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uff.ic.swlab.util.recall.dosso.datasets.deprecated;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

/**
 *
 * @author angelo
 */
public class BuildRecall {

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

    public static void main(String[] args) throws IOException {
        String nameService = "BSBM_10M";

        String serviceDatabase = String.format("http://semanticweb.inf.puc-rio.br:3030/%1$s/sparql", nameService);

        CalculateRecallRDFDatasets calculateRecall = new CalculateRecallRDFDatasets();
        calculateRecall.getReference(nameService, serviceDatabase);
        calculateRecall.getAnswer(nameService);

        HashMap<Integer, Double> mapRecall = calculateRecall.calculateRecall();

        ExportCSV(mapRecall, nameService);
    }

}
