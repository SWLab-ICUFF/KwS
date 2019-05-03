package uff.ic.aneves.kws;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Calendar;
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
import uff.ic.swlab.util.FusekiServer;

public class RunKWSQuery {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        
        String service = "http://localhost:3030/Mondial/sparql";
        String service2 = "http://localhost:3030/Mondial.benchmark/sparql";
        
        
        Workbook wb = new HSSFWorkbook();
        String datasetfolder = "Mondial";
        String dir = "./src/main/resources/benchmarks/Coffman/" + datasetfolder;
        File fileFolder = new File(dir);
        File[] listOfFiles = fileFolder.listFiles();
        for (int i = 0; i < listOfFiles.length; i++) {
            File file = listOfFiles[i];
            if (file.isFile() && file.getName().endsWith(".txt") && !file.getName().equals("1-topics.txt")) {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                String firstLine = bufferedReader.readLine();
                String KwS = firstLine.replace("#", "");
                String numberTxt = file.getName().replace(".txt", "");
                System.out.println(numberTxt + "---" + KwS);
                Integer linha = Integer.parseInt(numberTxt);
                String benchmark = String.format("urn:graph:kws:%1$03d:", linha);
                String filename = String.format("./src/main/resources/benchmarks/CIKM2019/Mondial/%1$03d.nq.gz", linha);
                String filename2 = String.format("./src/main/resources/benchmarks/CIKM2019/Mondial/stats.ttl", linha);
                RunKWS(numberTxt, KwS, wb, service, service2, benchmark, filename, filename2);
        
            }
          
        }
        wb.close();
    }

    private static void RunKWS(String numberTxt, String keywordQuery,
            Workbook wb, String service, String service2, String benchmark,
            String filename, String filename2) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("localhost", 3030);
        String queryString = "";

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/v2/kws_00_prepare.rq");
            fuseki.execUpdate(queryString, "Work.temp");
            fuseki.execUpdate(queryString, "KwS.stats");
        }

        Calendar t1 = Calendar.getInstance();

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/v2/kws_10_search.rq");
            queryString = queryString.format(queryString, service, keywordQuery, benchmark);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/v2/kws_30_rank.rq");
            queryString = queryString.format(queryString, keywordQuery);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        Calendar t2 = Calendar.getInstance();
        double seconds = Duration.between(t1.toInstant(), t2.toInstant()).toMillis() / 1000.0;
        System.out.println("");
        System.out.println(String.format("Elapsed time: %1$f seconds", seconds));

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/v2/kws_45_stats.rq");
            queryString = queryString.format(queryString, benchmark + "case", keywordQuery, seconds);
            fuseki.execUpdate(queryString, "KwS.stats");
        }

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/v2/kws_40_eval.rq");
            queryString = queryString.format(queryString, service, service2, benchmark);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        {
            Dataset dataset = fuseki.getDataset("Work.temp");
            bkpDataset(dataset, filename);
        }

        {
            Model model = fuseki.getModel("KwS.stats");
            model.setNsPrefix("urn", "urn:uuid:");
            model.setNsPrefix("kws", "urn:vocab:kws:");
            model.setNsPrefix("kwsg", "urn:graph:kws:");
            model.setNsPrefix("rdf", RDF.uri);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);
            writeModel(model, filename2);
        }
        String serviceURI = "http://localhost:3030/Work.temp/sparql";
        ExportExcel(serviceURI, benchmark, numberTxt, wb);
       
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
        String[] columns = {"position", "sol", "score", "size", "recall"};
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
                    .setCellValue(position);
            row.createCell(1)
                    .setCellValue(sol);
            row.createCell(2)
                    .setCellValue(score);
            row.createCell(3)
                    .setCellValue(size);
            if(recall_String.equals("Null"))
                row.createCell(4)
                        .setCellValue(recall_String);
            else
                row.createCell(4)
                        .setCellValue(recall);
            position++;

        }

        FileOutputStream fileOut = new FileOutputStream(dirResult);

        wb.write(fileOut);
        fileOut.close();

    }

}
