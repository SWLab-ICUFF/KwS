package uff.ic.swlab.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;
import org.apache.jena.vocabulary.XSD;

public class ConvertPageRankFile2 {

    public static void main(String[] args) throws IOException {
        String tdbDir = "./src/main/resources/tdb";
        String filename = "/Users/lapaesleme/local/var/temp/2019-11-09.allwiki.links.rank";
        new File(tdbDir).mkdirs();

        try (InputStream in = new FileInputStream(filename);
                Writer out = new OutputStreamWriter(
                        new GZIPOutputStream(
                                new BufferedOutputStream(
                                        new FileOutputStream(filename + ".nq.gz"))))) {

            Scanner scan = new Scanner(in);
            while (scan.hasNext()) {
                String subject = String.format("<http://www.wikidata.org/entity/%1$s>", scan.next().trim());
                String predicate = "<urn:vocab:kws:pageRank>";
                String object = String.format("\"%1$f\"^^<%2$s>", scan.nextDouble(), XSD.xdouble.getURI());
                String graph = "<urn:graph:kws:scores>";
                String line = String.format("%1$s %2$s %3$s %4$s .%n", subject, predicate, object, graph);

                out.write(line);
            }

        }
    }
}
