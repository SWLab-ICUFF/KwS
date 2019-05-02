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
import uff.ic.swlab.util.FusekiServer;

public class BuildBenchmark {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {

        try (InputStream in = new FileInputStream(new File("./src/main/resources/benchmarks/CIKM2019/Mondial/queries_.txt"));
                Scanner sc = new Scanner(in)) {

            int i = 0;
            while (sc.hasNext()) {
                String keywordQuery = sc.nextLine().trim();
                String benchmark = String.format("urn:graph:kws:%1$03d:", ++i);
                String filename = String.format("./src/main/resources/benchmarks/CIKM2019/Mondial/%1$03d.nq.gz", ++i);
                String service = "http://localhost:3030/Mondial/sparql";
                String service2 = "http://localhost:3030/Mondial.benchmark/sparql";

                run(service, service2, keywordQuery, benchmark, filename);
                if (i == 3)
                    break;
            }

        } finally {
        }
    }

    public static void run(String service, String service2, String keywordQuery, String benchmark, String filename) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("localhost", 3030);
        String queryString = "";

        Calendar t1 = Calendar.getInstance();

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/kws_10_search_v2.rq");
            queryString = queryString.format(queryString, service, keywordQuery, benchmark);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/kws_30_rank_v2.rq");
            queryString = queryString.format(queryString, keywordQuery);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        Calendar t2 = Calendar.getInstance();
        System.out.println("");
        System.out.println(String.format("Elapsed time: %1$f seconds", Duration.between(t1.toInstant(), t2.toInstant()).toMillis() / 1000.0));

        if (true) {
            queryString = readQuery("./src/main/sparql/KwS/kws_40_eval.rq");
            queryString = queryString.format(queryString, service, service2, benchmark);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        Dataset dataset = fuseki.getDataset("KwS.temp");
        writeDataset(dataset, filename);
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

    private static void writeDataset(Dataset dataset, String filename) throws FileNotFoundException, IOException {
        try (OutputStream out = new FileOutputStream(new File(filename));
                GZIPOutputStream out2 = new GZIPOutputStream(out)) {
            RDFDataMgr.write(out2, dataset, Lang.NQUADS);
            out2.finish();
            out.flush();
        } finally {
        }
    }
}
