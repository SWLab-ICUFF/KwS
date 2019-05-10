/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uff.ic.aneves.kws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Calendar;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;
import javax.naming.InvalidNameException;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.XSD;
import static uff.ic.swlab.kws.BuildBenchmark.run;
import uff.ic.swlab.util.FusekiServer;

/**
 *
 * @author angelo
 */
public class BuildBenchmarkV2 {
    
       public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        String service1 = "http://localhost:3030/IMDb2/sparql";
        String service2 = "http://localhost:3030/IMDb2.benchmark/sparql";
        String service3 = "http://localhost:3030/Work.temp/sparql";
        String kwsVersion = "v2";
        //String kwsVersion = "v2_2";
        String benchmark = "CIKM2019_1";
        //String benchmark = "CIKM2019_2";
        String rankingFilename = String.format("./src/main/resources/benchmarks/%1$s/Mondial/ranking.ttl", benchmark);

        new FusekiServer("localhost", 3030).execUpdate(readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_00_prepare.rq", kwsVersion)), "KwS.stats");

        try (InputStream in = new FileInputStream(new File(String.format("./src/main/resources/benchmarks/%1$s/Mondial/queries_.txt", benchmark)));
                Scanner sc = new Scanner(in)) {

            int i = 0;
            while (sc.hasNext()) {
                i++;
                String keywordQuery = sc.nextLine().trim();
                if (keywordQuery != null && !keywordQuery.equals("")) {
                    String benchmarkNS = String.format("urn:graph:kws:%1$03d:", i);
                    String benchmarkFilename = String.format("./src/main/resources/benchmarks/%1$s/IMDb2/%2$03d.nq.gz", benchmark, i);
                    run(kwsVersion, service1, service2, service3, keywordQuery, benchmarkNS, benchmarkFilename, rankingFilename);
                }
                System.out.println(i);
            }

        } finally {
        }
       }
       
       public static void run(String kwsVersion, String service1, String service2, String service3, String keywordQuery, String benchmarkNS, String filename, String filename2) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("localhost", 3030);
        String queryString = "";

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_00_prepare.rq", kwsVersion));
            fuseki.execUpdate(queryString, "Work.temp");
        }

        Calendar t1 = Calendar.getInstance();

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_10_search.rq", kwsVersion));
            queryString = queryString.format(queryString, service1, keywordQuery, benchmarkNS);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_20_rank.rq", kwsVersion));
            queryString = queryString.format(queryString, keywordQuery);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        Calendar t2 = Calendar.getInstance();
        double seconds = Duration.between(t1.toInstant(), t2.toInstant()).toMillis() / 1000.0;
        System.out.println("");
        System.out.println(String.format("Elapsed time: %1$f seconds", seconds));

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_40_finish.rq", kwsVersion));
            queryString = queryString.format(queryString, service1, service2, benchmarkNS);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_50_eval.rq", kwsVersion));
            queryString = queryString.format(queryString, service2, service3, benchmarkNS, keywordQuery, seconds);
            fuseki.execUpdate(queryString, "KwS.stats");
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
            model.setNsPrefix("time", "http://www.w3.org/2006/time#");
            model.setNsPrefix("rdf", RDF.uri);
            model.setNsPrefix("rdfs", RDFS.uri);
            model.setNsPrefix("xsd", XSD.NS);
            writeModel(model, filename2);
        }
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


    
}
