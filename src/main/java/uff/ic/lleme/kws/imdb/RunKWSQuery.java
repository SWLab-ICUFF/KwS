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

public class RunKWSQuery {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer();
        String kwsString = "(rio de janeiro Brazil)";
        String queryString = "";

        fuseki.execUpdate("drop all", "Work.temp");
        if (true) {
            queryString = readQuery("./src/main/sparql/kws_query.rq");
            queryString = queryString.format(queryString, kwsString);
            fuseki.execUpdate(queryString, "Work.temp");
        }

        {
            Model model = fuseki.getModel("Work.temp", "urn:graph:kws:temp");
            model.setNsPrefix("kws", "urn:vocab:kws:");
            model.setNsPrefix("kwsg", "urn:graph:kws:");
            model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            model.setNsPrefix("mond", "http://www.semwebtech.org/mondial/10/");
            model.setNsPrefix("meta", "http://www.semwebtech.org/mondial/10/meta#");
            writeModel(model, "./src/main/resources/dat/temp.ttl");
        }

        {
            Model model = fuseki.getModel("Work.temp");
            model.setNsPrefix("kws", "urn:vocab:kws:");
            model.setNsPrefix("kwsg", "urn:graph:kws:");
            model.setNsPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
            model.setNsPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
            model.setNsPrefix("mond", "http://www.semwebtech.org/mondial/10/");
            model.setNsPrefix("meta", "http://www.semwebtech.org/mondial/10/meta#");
            writeModel(model, "./src/main/resources/dat/result.ttl");
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
