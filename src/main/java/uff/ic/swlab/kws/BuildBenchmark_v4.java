/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 *
 * @author angelo
 */
public class BuildBenchmark_v4 {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        String database = "Mondial_ShortPaper";
        String service1 = String.format("http://semanticweb.inf.puc-rio.br:3030/%1$s/sparql", database);
        String service2 = "http://semanticweb.inf.puc-rio.br:3030/KwS.temp/sparql";

        String kwsVersion = "v4/1/1";
        String benchmark = "DEXA2020";

        try (InputStream in = new FileInputStream(new File(String.format("./src/main/resources/benchmarks/%1$s/%2$s/queries_.txt", benchmark, database)));
                Scanner sc = new Scanner(in)) {
            int i = 0;
            while (sc.hasNext()) {
                i++;
                String keywordQuery = sc.nextLine().trim();
                System.out.println(keywordQuery);
                if (keywordQuery != null && !keywordQuery.equals("")) {
                    String benchmarkNS = String.format("urn:graph:kws:%1$03d:", i);
                    String benchmarkFilename = String.format("./src/main/resources/benchmarks/%1$s/%2$s/%3$03d.nq.gz", benchmark, database, i);
                    run(kwsVersion, service1, service2, keywordQuery, benchmarkNS, benchmarkFilename);
                }
            }

        }

    }

    public static void run(String kwsVersion, String service1, String service2, String keywordQuery, String benchmarkNS, String filename) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("semanticweb.inf.puc-rio.br", 3030);
        String format_keywordQuery = keywordQuery.replaceAll("\\(|\\)", "");

        String queryString = "";
        System.out.println("=============================================GERANDO BENCHMARK PARA A PALVRA CHAVE " + keywordQuery + "=============================================");
        if (true) {
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_00_prepare.rq", kwsVersion));
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        Calendar t1 = Calendar.getInstance();

        //TODO: Algoritmo
        Calendar t2 = Calendar.getInstance();
        double seconds = Duration.between(t1.toInstant(), t2.toInstant()).toMillis() / 1000.0;
        System.out.println("");
        System.out.println(String.format("Elapsed time: %1$f seconds", seconds));

        //TODO: kws_42 da versao 3/1/2
        {
            Dataset dataset = fuseki.getDataset("KwS.temp");
            bkpDataset(dataset, filename);
        }

        System.out.println("============================================ FIM PARA A KEYWORD " + keywordQuery + " =============================================");

    }

    private static void bkpDataset(Dataset dataset, String filename) throws FileNotFoundException, IOException {
        try (OutputStream out = new FileOutputStream(new File(filename));
                GZIPOutputStream out2 = new GZIPOutputStream(out)) {
            RDFDataMgr.write(out2, dataset, Lang.NQUADS);
            out2.finish();
            out.flush();
        } finally {
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

}
