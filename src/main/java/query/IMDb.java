package query;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.naming.InvalidNameException;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;

public class IMDb {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer();
        String queryString = readQuery("./resources/sparql/q1_2.rq");
        Model model = fuseki.execConstruct(queryString, "IMDb");

        if (true) {
            queryString = readQuery("./resources/sparql/q2.rq");
            Query query = QueryFactory.create();
            QueryFactory.parse(query, queryString, "", Syntax.syntaxSPARQL_11);
            QueryExecution qexec = QueryExecutionFactory.create(query, model);
            qexec.execConstruct(model);
        }

        fuseki.putModel(model, "Result");
        writeModel(model, "./resources/dat/result.ttl");

        if (true) {
            queryString = readQuery("./resources/sparql/q3.rq");
            Query query = QueryFactory.create();
            QueryFactory.parse(query, queryString, "", Syntax.syntaxSPARQL_11);
            QueryExecution qexec = QueryExecutionFactory.create(query, model);
            model = qexec.execConstruct();
        }

        fuseki.putModel(model, "Result2");
        writeModel(model, "./resources/dat/result2.ttl");

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
