package uff.ic.lleme.kws.imdb;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.naming.InvalidNameException;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import uff.ic.lleme.util.FusekiServer;

public class IMDb {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer();
        String kwsString = "(actress \\\"out of africa\\\"^2 Meryl Streep)";
        String queryString = "";

        fuseki.execUpdate("clear all", "Temp");
        fuseki.execUpdate("clear all", "Result");

        if (true) {
            queryString = readQuery("./resources/sparql/q0.rq");
            queryString = queryString.format(queryString, kwsString);
            fuseki.execUpdate(queryString, "Temp");
        }

        if (true)
            for (int i = 0; i < 50; i++) {
                queryString = readQuery("./resources/sparql/q30.rq");
                queryString = queryString.format(queryString, kwsString);
                fuseki.execUpdate(queryString, "Temp");
            }

        if (true) {
            queryString = readQuery("./resources/sparql/q40.rq");
            queryString = queryString.format(queryString, kwsString);
            fuseki.execUpdate(queryString, "Result");
        }

        Model model = fuseki.getModel("Temp");
        writeModel(model, "./resources/dat/temp.ttl");
        model = fuseki.getModel("Result");
        writeModel(model, "./resources/dat/result.ttl");

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
