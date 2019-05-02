package uff.ic.swlab.kws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Calendar;
import java.util.zip.GZIPOutputStream;
import javax.naming.InvalidNameException;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import uff.ic.swlab.util.FusekiServer;

public class RunKWSQuery_V2 {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        run(args);
    }

    public static void run(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("localhost", 3030);
        String kwsString = "muritius india";
        String benchmark = "urn:graph:kws:043:";
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
            Dataset dataset = fuseki.getDataset("Work.temp");
            writeDataset(dataset, "./src/main/resources/benchmarks/CIKM2019/Mondial/043.nq.gz");
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
