/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uff.ic.aneves.kws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Calendar;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;
import javax.naming.InvalidNameException;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import static uff.ic.swlab.kws.BuildBenchmark.run;
import uff.ic.swlab.util.FusekiServer;

/**
 *
 * @author angelo
 */
public class BuildBenchmark {
    
    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        
        String version = "v2";
        String benchmark = "CoffmanRDF_1";
        Workbook wb = new HSSFWorkbook();
        new FusekiServer("localhost", 3030).execUpdate(readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_00_prepare.rq", version)), "KwS.stats");

        try (InputStream in = new FileInputStream(new File(String.format("./src/main/resources/benchmarks/%1$s/Mondial/queries_.txt", benchmark)));
                Scanner sc = new Scanner(in)) {

            int i = 0;
            while (sc.hasNext()) {
                i++;
                String keywordQuery = sc.nextLine().trim();
                if (keywordQuery != null && !keywordQuery.equals("")) {
                    String benchmarkNS = String.format("urn:graph:kws:%1$03d:", i);
                    String filename = String.format("./src/main/resources/benchmarks/%1$s/Mondial/%2$03d.nq.gz", benchmark, i);
                    String filename2 = String.format("./src/main/resources/benchmarks/%1$s/Mondial/ranking.ttl", benchmark);
                    String service1 = "http://localhost:3030/Mondial/sparql";
                    String service2 = "http://localhost:3030/Mondial.benchmark/sparql";
                    String service3 = "http://localhost:3030/KwS.temp/sparql";

                    run(version, service1, service2, service3, keywordQuery, benchmarkNS, filename, filename2, wb, i);
                }
               
            }

        } finally {
        }
        wb.close();
    }

     public static void run(String version, String service1, String service2, String service3, String keywordQuery, String benchmarkNS, String filename, String filename2, Workbook wb, Integer count) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("localhost", 3030);
        String queryString = "";

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_00_prepare.rq", version));
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        Calendar t1 = Calendar.getInstance();

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_10_search.rq", version));
            queryString = queryString.format(queryString, service1, keywordQuery, benchmarkNS);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_30_rank.rq", version));
            queryString = queryString.format(queryString, keywordQuery);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        Calendar t2 = Calendar.getInstance();
        double seconds = Duration.between(t1.toInstant(), t2.toInstant()).toMillis() / 1000.0;
        System.out.println("");
        System.out.println(String.format("Elapsed time: %1$f seconds", seconds));

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_40_finish.rq", version));
            queryString = queryString.format(queryString, service1, service2, benchmarkNS);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_50_eval.rq", version));
            queryString = queryString.format(queryString, service2, service3, benchmarkNS, keywordQuery, seconds);
            fuseki.execUpdate(queryString, "KwS.stats");
        }

        {
            Dataset dataset = fuseki.getDataset("KwS.temp");
            bkpDataset(dataset, filename);
        }

        {
            Model model = fuseki.getModel("KwS.stats");
            model.setNsPrefix("urn", "urn:uuid:");
            model.setNsPrefix("kws", "urn:vocab:kws:");
            model.setNsPrefix("kwsg", "urn:graph:kws:");
            model.setNsPrefix("time", "http://www.w3.org/2006/time#");
            model.setNsPrefix("rdf", RDF.uri);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);
            writeModel(model, filename2);
        }
        
        
        String serviceURI = "http://localhost:3030/Work.temp/sparql";
        String numberTxt = Integer.toString(count);
        ExportExcel(serviceURI, benchmarkNS, numberTxt, wb);
       
    }
    
    
    private static String readQuery(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }
        return new String(data, "UTF-8");
    }

    private static void writeModel(Model model, String filename) throws FileNotFoundException {
        File file = new File(filename);
        OutputStream out = new FileOutputStream(file);
        RDFDataMgr.write(out, model, RDFFormat.TURTLE_PRETTY);
    }

    private static void bkpDataset(Dataset dataset, String filename) throws FileNotFoundException, IOException {
        try (OutputStream out = new FileOutputStream(new File(filename));
                GZIPOutputStream out2 = new GZIPOutputStream(out)) {
            RDFDataMgr.write(out2, dataset, Lang.NQUADS);
            out2.finish();
            out.flush();
        } finally {
        }
    }

    private static void ExportExcel(String serviceURI, String benchmark,
            String numberTxt, Workbook wb) throws FileNotFoundException, IOException {
        String dirResult = "./src/main/resources/dat/Work.temp/resultsPrograma.csv";
        String[] columns = {"case", "position", "sol", "score", "size", "recall"};
        Sheet sheet = wb.createSheet(numberTxt);

        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < columns.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(columns[i]);

        }

        int rowNum = 1;
        String query = "    \n"
                + "prefix : <urn:vocab:kws:>\n"
                + "prefix kws: <urn:vocab:kws:>\n"
                + "prefix kwsg: <urn:graph:kws:>\n"
                + "prefix rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n"
                + "prefix rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "prefix meta: <http://www.semwebtech.org/mondial/10/meta#>\n"
                + "prefix text: <http://jena.apache.org/text#>\n"
                + "prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n"
                + "PREFIX fn: <java:uff.ic.swlab.jena.sparql.function.>\n"
                + "\n"
                + "select ?sol (?_score as ?score) (?_size as ?size) (?_recall/?reference as ?recall)\n"
                + "{\n"
                + "  {select ?sol (min(?score) as ?_score) (min(?size) as ?_size) (count(distinct ?answer) as ?_recall)\n"
                + "    where {\n"
                + "      graph ?sol {\n"
                + "        ?sol kws:score ?score; kws:initialSize ?size.\n"
                + "        optional {graph ?betterSol {?betterSol kws:score ?score2; kws:initialSize ?size2.}}\n"
                + "        filter(?score2 >= ?score || (?score2 = ?score && ?size2 <= ?size))\n"
                + "        optional {graph ?betterSol {?betterSol kws:answers ?answer.}}\n"
                + "      }\n"
                + "    }\n"
                + "    group by ?sol\n"
                + "  }\n"
                + "  {\n"
                + "    select (count(distinct ?g) as ?reference)\n"
                + "    where {\n"
                + "      service <http://localhost:3030/Mondial.benchmark/sparql> {\n"
                + "        graph ?g {?s ?p [].}\n"
                 + "        filter (regex(str(?g),\"" + benchmark + "\"))\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n"
                + "order by desc(?_score)";
        
        QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI, query);
        ResultSet resultSet = q.execSelect();
        Integer position = 1;
        while (resultSet.hasNext()) {
            QuerySolution soln = resultSet.nextSolution();
            String sol = String.valueOf(soln.get("sol"));
            Literal scoreLiteral = soln.getLiteral("score");
            Float score = scoreLiteral.getFloat();
            Literal sizeLiteral = soln.getLiteral("size");
            Integer size = sizeLiteral.getInt();
            Literal recallLiteral = soln.getLiteral("recall");
            String recall_String = "";
            Double recall = 0.0;
            if(recallLiteral == null){
                recall_String = "Null";
            }else{
                recall = recallLiteral.getDouble();
            }
            
            //System.out.println(sol + "----" + score  + "----" + size  + "----" + recall);
            Row row = sheet.createRow(rowNum++);
            row.createCell(0)
                    .setCellValue(numberTxt);
            row.createCell(1)
                    .setCellValue(position);
            row.createCell(2)
                    .setCellValue(sol);
            row.createCell(3)
                    .setCellValue(score);
            row.createCell(4)
                    .setCellValue(size);
            if(recall_String.equals("Null"))
                row.createCell(5)
                        .setCellValue(recall_String);
            else
                row.createCell(5)
                        .setCellValue(recall);
            position++;

        }

        FileOutputStream fileOut = new FileOutputStream(dirResult);

        wb.write(fileOut);
        fileOut.close();

    }
    
}
