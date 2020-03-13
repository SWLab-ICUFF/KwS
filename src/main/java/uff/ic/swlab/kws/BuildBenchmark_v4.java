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
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
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
        String path_database = "Mondial"; //IMDb DBpedia

        try (InputStream in = new FileInputStream(new File(String.format("./src/main/resources/benchmarks/%1$s/%2$s/queries_.txt", benchmark, path_database)));
                Scanner sc = new Scanner(in)) {
            int i = 0;
            while (sc.hasNext()) {
                i++;
                String keywordQuery = sc.nextLine().trim();
                System.out.println(keywordQuery);
                if (keywordQuery != null && !keywordQuery.equals("")) {
                    String benchmarkNS = String.format("urn:graph:kws:%1$03d:", i);
                    String benchmarkFilename = String.format("./src/main/resources/benchmarks/%1$s/%2$s/%3$03d.nq.gz", benchmark, path_database, i);
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
        
        
        System.out.println("Buscando seeds");
        if (true) {
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_10_search.rq", kwsVersion)); //ok
            queryString = String.format(queryString, service1, keywordQuery);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        if (true) {
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_20_literais.rq", kwsVersion)); //ok
            queryString = String.format(queryString, service1, keywordQuery);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

//        if (true) {
//            String newKws;
//            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_30_new_seeds.rq", kwsVersion)); //ok
//            queryString = String.format(queryString, keywordQuery);
//            ResultSet result = fuseki.execSelect(queryString, "KwS.temp");
//
//            newKws = null;
//            while (result.hasNext()) {
//                QuerySolution soln = result.nextSolution();
//
//                newKws = String.valueOf(soln.get("new_kws"));
//                if (newKws != null) {
//                    System.out.println("Buscando novas seeds");
//                    queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_10_search.rq", kwsVersion)); //ok
//                    queryString = String.format(queryString, service1, newKws);
//                    fuseki.execUpdate(queryString, "KwS.temp");
//
//                }
//
//                if (true) {
//                    queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_20_literais.rq", kwsVersion)); //ok
//                    queryString = String.format(queryString, service1, keywordQuery);
//                    fuseki.execUpdate(queryString, "KwS.temp");
//                }
//
//            }
//
//        }
        
        if (true) {
            System.out.println("Buscando propriedades que match");
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_40_search_properties_match.rq", kwsVersion)); //ok
            queryString = String.format(queryString, service1);
            fuseki.execUpdate(queryString, "KwS.temp");
        }
        
        if (true) {
            System.out.println("Gerando os pares, calculando scores");
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_50_search.rq", kwsVersion)); //ok
            queryString = String.format(queryString, format_keywordQuery, benchmarkNS);
            fuseki.execUpdate(queryString, "KwS.temp");

        }
        
        if (true) {
            System.out.println("Filtrando o conjunto de soluções");
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_60_search.rq", kwsVersion)); //ok
            queryString = String.format(queryString);
            fuseki.execUpdate(queryString, "KwS.temp");

        }
        
         if (true) {
            System.out.println("Gerando os caminhos....");
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_70_search.rq", kwsVersion)); //ok
            queryString = String.format(queryString, service1, service2);
            fuseki.execUpdate(queryString, "KwS.temp");

        }
         
        if (true) {
            System.out.println("Separando os grafos conexos");
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_75_split.rq", kwsVersion)); //ok
            queryString = String.format(queryString);
            fuseki.execUpdate(queryString, "KwS.temp");
        }
        
        if (true) {
            System.out.println("Calculando o score das soluções..."); //ok
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_80_search.rq", kwsVersion));
            queryString = String.format(queryString, keywordQuery, service1, service2, format_keywordQuery, "KwS.temp");
            fuseki.execUpdate(queryString, "KwS.temp");
        }
        
         if (true) {
            System.out.println("Calculando o score final..."); //ok
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_90_finish.rq", kwsVersion));
            queryString = String.format(queryString, "KwS.temp");
            fuseki.execUpdate(queryString, "KwS.temp");
        }
       
        Calendar t2 = Calendar.getInstance();
        double seconds = Duration.between(t1.toInstant(), t2.toInstant()).toMillis() / 1000.0;
        System.out.println("");
        System.out.println(String.format("Elapsed time: %1$f seconds", seconds));

        if (true) {
            System.out.println("Exportando Resultado..."); //ok
            queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_95_finish.rq", kwsVersion));
            queryString = String.format(queryString, benchmarkNS, keywordQuery, seconds);
            fuseki.execUpdate(queryString, "KwS.temp");
        }
        
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
