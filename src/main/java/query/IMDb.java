package query;

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

public class IMDb {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer();
        String queryString = readQuery("./resources/sparql/q1.rq");
        Model model = fuseki.execConstruct(queryString, "IMDb");
        queryString = readQuery("./resources/sparql/q1.rq");
        model.add(fuseki.execConstruct(queryString, "IMDb"));
        queryString = readQuery("./resources/sparql/q2.rq");
        model.add(fuseki.execConstruct(queryString, "IMDb"));
        queryString = readQuery("./resources/sparql/q3.rq");
        model.add(fuseki.execConstruct(queryString, "IMDb"));
        queryString = readQuery("./resources/sparql/q4.rq");
        model.add(fuseki.execConstruct(queryString, "IMDb"));
        queryString = readQuery("./resources/sparql/q5.rq");
        model.add(fuseki.execConstruct(queryString, "IMDb"));
        queryString = readQuery("./resources/sparql/q6.rq");
        model.add(fuseki.execConstruct(queryString, "IMDb"));
        queryString = readQuery("./resources/sparql/q7.rq");
        model.add(fuseki.execConstruct(queryString, "IMDb"));
        queryString = readQuery("./resources/sparql/q8.rq");
        model.add(fuseki.execConstruct(queryString, "IMDb"));
        fuseki.putModel(model, "Result");
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
