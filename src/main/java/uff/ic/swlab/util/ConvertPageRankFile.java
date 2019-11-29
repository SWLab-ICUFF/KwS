package uff.ic.swlab.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPOutputStream;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.ReadWrite;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResourceFactory;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.tdb2.TDB2Factory;

public class ConvertPageRankFile {

    public static void main(String[] args) throws IOException {
        try {
            String tdbDir = "./src/main/resources/tdb";
            String filename = "/Users/lapaesleme/local/var/temp/2019-11-09.allwiki.links.rank";
            new File(tdbDir).mkdirs();
            Property pageRank = ResourceFactory.createProperty("urn:vocab:kws:pageRank");

            Dataset dataset = TDB2Factory.connectDataset(tdbDir + "/scores");

            if (true) {
                dataset.begin(ReadWrite.WRITE);
                dataset.removeNamedModel("urn:vocab:kws:scores");
                Model model = dataset.getNamedModel("urn:vocab:kws:scores");
                model.setNsPrefix("kws", "urn:vocab:kws:");
                dataset.commit();

                int counter = 0;
                InputStream in = new FileInputStream(filename);
                Scanner scan = new Scanner(in);
                dataset.begin(ReadWrite.WRITE);
                while (scan.hasNext()) {
                    String id = String.format("http://www.wikidata.org/entity/%1$s", scan.next());
                    Double score = scan.nextDouble();
                    model.addLiteral(ResourceFactory.createResource(id), pageRank, score.doubleValue());
                    if (counter++ % 500000 == 0) {
                        dataset.commit();
                        dataset.begin(ReadWrite.WRITE);
                    }
                }
                if (dataset.isInTransaction())
                    dataset.commit();
            }

            try (GZIPOutputStream out = new GZIPOutputStream(
                    new BufferedOutputStream(
                            new FileOutputStream(filename + ".nq.gz")))) {
                RDFDataMgr.write(out, dataset, RDFFormat.NQUADS);
            }

            dataset.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConvertPageRankFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
