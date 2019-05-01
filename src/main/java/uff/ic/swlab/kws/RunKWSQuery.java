package uff.ic.swlab.kws;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.util.Calendar;
import javax.naming.InvalidNameException;
import org.apache.jena.query.Dataset;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import uff.ic.swlab.util.FusekiServer;

public class RunKWSQuery {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("localhost", 3030);
        String kwsString = "mauritius india";
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
}
