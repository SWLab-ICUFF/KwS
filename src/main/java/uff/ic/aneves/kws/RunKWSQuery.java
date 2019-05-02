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
import javax.naming.InvalidNameException;
import org.apache.commons.io.FileUtils;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import uff.ic.swlab.util.FusekiServer;

public class RunKWSQuery {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
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
                RunKWS(numberTxt, KwS);
            }

        }
    }

    private static void RunKWS(String numberTxt, String KwS) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("localhost", 3030);
        String kwsString = KwS;
        String benchmark = "urn:graph:kws:" + numberTxt + ":";
        String queryString = "";
        
        Calendar t1 = Calendar.getInstance();

        if (false) {
            queryString = readQuery("./src/main/sparql/KwS/kws_10_search.rq");
            queryString = queryString.format(queryString, kwsString);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/kws_20_search_v2.rq");
            queryString = queryString.format(queryString, kwsString, benchmark);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/kws_30_rank_v2.rq");
            queryString = queryString.format(queryString, kwsString, benchmark);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        Calendar t2 = Calendar.getInstance();
        System.out.println("");
        System.out.println(String.format("Elapsed time: %1$f seconds", Duration.between(t1.toInstant(), t2.toInstant()).toMillis() / 1000.0));

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/kws_40_eval.rq");
            queryString = queryString.format(queryString, benchmark);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        {
            Model model = fuseki.getModel("Work.temp", "urn:graph:kws:seeds");
            model.setNsPrefix("urn", "urn:uuid:");
            model.setNsPrefix("kws", "urn:vocab:kws:");
            model.setNsPrefix("kwsg", "urn:graph:kws:");
            model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            model.setNsPrefix("mond", "http://www.semwebtech.org/mondial/10/");
            model.setNsPrefix("meta", "http://www.semwebtech.org/mondial/10/meta#");
            writeModel(model, "./src/main/resources/dat/Work.temp/seeds.ttl");
        }

        {
            Model model = fuseki.getModel("Work.temp", "urn:graph:kws:temp");
            model.setNsPrefix("urn", "urn:uuid:");
            model.setNsPrefix("kws", "urn:vocab:kws:");
            model.setNsPrefix("kwsg", "urn:graph:kws:");
            model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            model.setNsPrefix("mond", "http://www.semwebtech.org/mondial/10/");
            model.setNsPrefix("meta", "http://www.semwebtech.org/mondial/10/meta#");
            writeModel(model, "./src/main/resources/dat/Work.temp/temp.ttl");
        }

        {
            Model model = fuseki.getModel("Work.temp", "urn:graph:kws:temp2");
            model.setNsPrefix("urn", "urn:uuid:");
            model.setNsPrefix("kws", "urn:vocab:kws:");
            model.setNsPrefix("kwsg", "urn:graph:kws:");
            model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            model.setNsPrefix("mond", "http://www.semwebtech.org/mondial/10/");
            model.setNsPrefix("meta", "http://www.semwebtech.org/mondial/10/meta#");
            writeModel(model, "./src/main/resources/dat/Work.temp/temp2.ttl");
        }

        {
            Model model = fuseki.getModel("Work.temp", "urn:graph:kws:temp3");
            model.setNsPrefix("urn", "urn:uuid:");
            model.setNsPrefix("kws", "urn:vocab:kws:");
            model.setNsPrefix("kwsg", "urn:graph:kws:");
            model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            model.setNsPrefix("mond", "http://www.semwebtech.org/mondial/10/");
            model.setNsPrefix("meta", "http://www.semwebtech.org/mondial/10/meta#");
            writeModel(model, "./src/main/resources/dat/Work.temp/temp3.ttl");
        }

        {
            Model model = fuseki.getModel("Work.temp", "urn:graph:kws:temp4");
            model.setNsPrefix("urn", "urn:uuid:");
            model.setNsPrefix("kws", "urn:vocab:kws:");
            model.setNsPrefix("kwsg", "urn:graph:kws:");
            model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            model.setNsPrefix("mond", "http://www.semwebtech.org/mondial/10/");
            model.setNsPrefix("meta", "http://www.semwebtech.org/mondial/10/meta#");
            writeModel(model, "./src/main/resources/dat/Work.temp/temp4.ttl");
        }

        {
            Dataset dataset = fuseki.getDataset("Work.temp");
//            model.setNsPrefix("urn", "urn:uuid:");
//            model.setNsPrefix("kws", "urn:vocab:kws:");
//            model.setNsPrefix("kwsg", "urn:graph:kws:");
//            model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
//            model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
//            model.setNsPrefix("mond", "http://www.semwebtech.org/mondial/10/");
//            model.setNsPrefix("meta", "http://www.semwebtech.org/mondial/10/meta#");
            writeDataset(dataset, "./src/main/resources/dat/Work.temp/dump.trig");
        }
        
        String serviceURI = "http://localhost:3030/Work.temp";
        ExportExcel(serviceURI, benchmark, kwsString);

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

    private static void writeDataset(Dataset dataset, String filename) throws FileNotFoundException {
        File file = new File(filename);
        OutputStream out = new FileOutputStream(file);
        RDFDataMgr.write(out, dataset, RDFFormat.TRIG_PRETTY);
    }
    
    private static void ExportExcel(String serviceURI, String benchmark, String kwsString){
        String query = "prefix : <urn:vocab:kws:>\n"
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
                + "        ?sol kws:score ?score; kws:size ?size.\n"
                + "        optional {graph ?betterSol {?betterSol kws:score ?score2; kws:size ?size2.}}\n"
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
                + "        filter (regex(str(?g),\""+benchmark+"\"))\n"
                + "      }\n"
                + "    }\n"
                + "  }\n"
                + "}\n"
                + "order by desc(?_score)";
        QueryExecution q = QueryExecutionFactory.sparqlService(serviceURI, query);
        ResultSet resultSet = q.execSelect();
        while (resultSet.hasNext()) {
            QuerySolution soln = resultSet.nextSolution();
            String sol =  String.valueOf(soln.get("sol"));
            Literal scoreLiteral = soln.getLiteral("score");
            Float score = scoreLiteral.getFloat();
            Literal sizeLiteral = soln.getLiteral("size");
            Integer size = sizeLiteral.getInt();
            Literal recallLiteral = soln.getLiteral("recall");
            Double recall = recallLiteral.getDouble();
            System.out.println(sol + "----" + score  + "----" + size  + "----" + recall);
        }


    }

}
