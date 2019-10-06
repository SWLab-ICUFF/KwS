package uff.ic.swlab.kws;

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
import uff.ic.swlab.util.FusekiServer;

public class BuildBenchmarkV2 {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {

        String service1 = "http://localhost:3030/Mondial/sparql";
        String service2 = "http://localhost:3030/Mondial.benchmark/sparql";
        String service3 = "http://localhost:3030/KwS.temp/sparql";
        String kwsVersion;
        String benchmark;
        String rankingFilename;

        for (int c : new int[]{9211}) {
            switch (c) {
                case 211:
                    kwsVersion = "v2/1/1";
                    benchmark = "CIKM2019_1_1";
                    break;
                case 212:
                    kwsVersion = "v2/1/2";
                    benchmark = "CIKM2019_1_2";
                    break;
                case 221:
                    kwsVersion = "v2/2/1";
                    benchmark = "CIKM2019_2_1";
                    break;
                case 222:
                    kwsVersion = "v2/2/2";
                    benchmark = "CIKM2019_2_2";
                    break;
                case 7212:
                    kwsVersion = "v2/1/2";
                    benchmark = "CIKM2019_teste";
                    break;
                case 8211:
                    kwsVersion = "v2/1/1";
                    benchmark = "CoffmanRDF_2_1_1";
                    break;
                case 8222:
                    kwsVersion = "v2/2/2";
                    benchmark = "CoffmanRDF_2_2_2";
                    break;
                case 9211:
                    kwsVersion = "v2/1/1";
                    benchmark = "CoffmanRDF_teste";
                    break;
                default:
                    kwsVersion = "v2/1/1";
                    benchmark = "CIKM2019_1_1";
                    break;
            }
            rankingFilename = String.format("./src/main/resources/benchmarks/%1$s/Mondial/ranking.ttl", benchmark);
            System.out.println(benchmark);

            new FusekiServer("localhost", 3030).execUpdate(readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_00_prepare.rq", kwsVersion)), "KwS.stats");

            try (InputStream in = new FileInputStream(new File(String.format("./src/main/resources/benchmarks/%1$s/Mondial/queries_.txt", benchmark)));
                    Scanner sc = new Scanner(in)) {

                int i = 0;
                while (sc.hasNext()) {
                    i++;
                    String keywordQuery = sc.nextLine().trim();
                    if (keywordQuery != null && !keywordQuery.equals("")) {
                        String benchmarkNS = String.format("urn:graph:kws:%1$03d:", i);
                        String benchmarkFilename = String.format("./src/main/resources/benchmarks/%1$s/Mondial/%2$03d.nq.gz", benchmark, i);
                        run(kwsVersion, service1, service2, service3, keywordQuery, benchmarkNS, benchmarkFilename, rankingFilename);
                    }
                }

            } finally {
            }
        }
    }

    public static void run(String kwsVersion, String service1, String service2, String service3, String keywordQuery, String benchmarkNS, String benchmarkFilename, String rankingFilename) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("localhost", 3030);
        String queryString = "";

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_00_prepare.rq", kwsVersion));
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        Calendar t1 = Calendar.getInstance();
        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_10_search.rq", kwsVersion));
            queryString = queryString.format(queryString, service1, keywordQuery, benchmarkNS);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_20_rank.rq", kwsVersion));
            queryString = queryString.format(queryString, service1, keywordQuery);
            fuseki.execUpdate(queryString, "KwS.temp");
        }
        Calendar t2 = Calendar.getInstance();

        double seconds = Duration.between(t1.toInstant(), t2.toInstant()).toMillis() / 1000.0;
        System.out.println("");
        System.out.println(String.format("Elapsed time: %1$f seconds", seconds));

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_40_finish.rq", kwsVersion));
            queryString = queryString.format(queryString, service1, service2, benchmarkNS);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_50_eval.rq", kwsVersion));
            queryString = queryString.format(queryString, service2, service3, benchmarkNS, keywordQuery, seconds);
            fuseki.execUpdate(queryString, "KwS.stats");
        }

        if (false) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_60_finish.rq", kwsVersion));
            queryString = queryString.format(queryString, service1, service2, benchmarkNS);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        {
            Dataset dataset = fuseki.getDataset("KwS.temp");
            bkpDataset(dataset, benchmarkFilename);
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
            writeModel(model, rankingFilename);
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
