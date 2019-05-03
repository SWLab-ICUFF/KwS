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

public class BuildBenchmark {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {

        //new FusekiServer("localhost", 3030).execUpdate(readQuery("./src/main/sparql/KwS/v2/kws_00_prepare.rq"), "KwS.stats");
        try (InputStream in = new FileInputStream(new File("./src/main/resources/benchmarks/CIKM2019_1/Mondial/queries_.txt"));
                Scanner sc = new Scanner(in)) {

            int i = 0;
            while (sc.hasNext()) {
                i++;
                if (i < 23)
                    continue;
                String keywordQuery = sc.nextLine().trim();
                if (keywordQuery != null && !keywordQuery.equals("")) {
                    String benchmark = String.format("urn:graph:kws:%1$03d:", i);
                    String filename = String.format("./src/main/resources/benchmarks/CIKM2019_1/Mondial/%1$03d.nq.gz", i);
                    String filename2 = "./src/main/resources/benchmarks/CIKM2019_1/Mondial/ranking.ttl";
                    String service1 = "http://localhost:3030/Mondial/sparql";
                    String service2 = "http://localhost:3030/Mondial.benchmark/sparql";
                    String service3 = "http://localhost:3030/KwS.temp/sparql";

                    run(service1, service2, service3, keywordQuery, benchmark, filename, filename2);
                }
            }

        } finally {
        }
    }

    public static void run(String service1, String service2, String service3, String keywordQuery, String benchmark, String filename, String filename2) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("localhost", 3030);
        String queryString = "";

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/v2/kws_00_prepare.rq");
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        Calendar t1 = Calendar.getInstance();

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/v2/kws_10_search.rq");
            queryString = queryString.format(queryString, service1, keywordQuery, benchmark);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/v2/kws_30_rank.rq");
            queryString = queryString.format(queryString, keywordQuery);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        Calendar t2 = Calendar.getInstance();
        double seconds = Duration.between(t1.toInstant(), t2.toInstant()).toMillis() / 1000.0;
        System.out.println("");
        System.out.println(String.format("Elapsed time: %1$f seconds", seconds));

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/v2/kws_40_finish.rq");
            queryString = queryString.format(queryString, service1, service2, benchmark);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/v2/kws_50_eval.rq");
            queryString = queryString.format(queryString, service2, service3, benchmark, keywordQuery, seconds);
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
