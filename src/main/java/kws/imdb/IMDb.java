package kws.imdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.naming.InvalidNameException;
import kws.FusekiServer;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

public class IMDb {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer();
        String kwsString = "(actress \\\"out of africa\\\"^3 Meryl Streep)";
        String queryString = "";

        fuseki.execUpdate("clear all", "Temp");
        fuseki.execUpdate("clear all", "Result");

        if (true) {
            queryString = readQuery("./resources/sparql/q1.rq");
            queryString = queryString.format(queryString, kwsString);
            fuseki.execUpdate(queryString, "Temp");
        }

        if (true) {
            queryString = readQuery("./resources/sparql/q2.rq");
            queryString = queryString.format(queryString, kwsString);
            fuseki.execUpdate(queryString, "Temp");
        }

        if (true) {
            queryString = readQuery("./resources/sparql/q3.rq");
            queryString = queryString.format(queryString, kwsString);
            fuseki.execUpdate(queryString, "Result");
        }

        if (true) {
            Model model = ModelFactory.createDefaultModel();
            queryString = "construct {?s ?p ?o.} where {?s ?p ?o.}";
            model = fuseki.execConstruct(queryString, "Result");
            writeModel(model, "./resources/dat/result.ttl");
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
}
