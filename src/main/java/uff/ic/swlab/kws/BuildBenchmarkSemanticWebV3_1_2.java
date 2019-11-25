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
import java.util.Calendar;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;
import javax.naming.InvalidNameException;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import uff.ic.swlab.util.FusekiServer;

/**
 *
 * @author angelo
 */
public class BuildBenchmarkSemanticWebV3_1_2 {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException {
        String service1 = "http://semanticweb.inf.puc-rio.br:3030/Mondial_ShortPaper/sparql";
        String service2 = "http://semanticweb.inf.puc-rio.br:3030/KwS.temp/sparql";

        String kwsVersion = "v3/1/2";
        String benchmark = "EDBT2020_v3_1_2";

        System.out.println(benchmark);

        try (InputStream in = new FileInputStream(new File(String.format("./src/main/resources/benchmarks/%1$s/Mondial/queries_.txt", benchmark)));
                Scanner sc = new Scanner(in)) {
            int i = 0;
            while (sc.hasNext()) {
                i++;
                String keywordQuery = sc.nextLine().trim();
                System.out.println(keywordQuery);
                if (keywordQuery != null && !keywordQuery.equals("")) {
                    String benchmarkNS = String.format("urn:graph:kws:%1$03d:", i);
                    String benchmarkFilename = String.format("./src/main/resources/benchmarks/%1$s/Mondial/%2$03d.nq.gz", benchmark, i);
                    run(kwsVersion, service1, service2, keywordQuery, benchmarkNS, benchmarkFilename);
                }
            }

        }
    }

    public static void run(String kwsVersion, String service1, String service2, String keywordQuery, String benchmarkNS, String filename) throws FileNotFoundException, IOException, InvalidNameException {
        FusekiServer fuseki = new FusekiServer("semanticweb.inf.puc-rio.br", 3030);
        String queryString = "";
        System.out.println("GERANDO BENCHMARK PARA A PALVRA CHAVE "+ keywordQuery);
        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_00_prepare.rq", kwsVersion)); //ok
            fuseki.execUpdate(queryString, "KwS.temp");
        }
        Calendar t1 = Calendar.getInstance();
        
        System.out.println("Buscando seeds");
        if (true) {
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_01_search.rq", kwsVersion)); //ok
            queryString = String.format(queryString, service1, keywordQuery);
            fuseki.execUpdate(queryString, "KwS.temp");
        }

        if (true) {
            String newKws;
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_02_search.rq", kwsVersion)); //ok
            queryString = String.format(queryString, keywordQuery); 
            ResultSet result = fuseki.execSelect(queryString, "KwS.temp");
            
            newKws = null;
            QuerySolution soln = result.nextSolution();
            newKws = String.valueOf(soln.get("new_kws"));
            if (newKws != null){
                System.out.println("Buscando novas seeds");
                queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_01_search.rq", kwsVersion)); //ok
                queryString = String.format(queryString, service1, newKws);
                fuseki.execUpdate(queryString, "KwS.temp");
                
            } 
            
            
        }

        if (true) {
            System.out.println("Buscando propriedades que match");
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_05_search.rq", kwsVersion)); //ok
            queryString = String.format(queryString, service1);
            fuseki.execUpdate(queryString, "KwS.temp");
        }
        
        if (true){
            System.out.println("Gerando os pares, calculando scores");
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_10_search.rq", kwsVersion)); //ok
            queryString = String.format(queryString, keywordQuery, benchmarkNS);
            fuseki.execUpdate(queryString, "KwS.temp");
            
        }
        
        if (true){
            System.out.println("Filtrando o conjunto de soluções");
            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_12_search.rq", kwsVersion)); //ok
            queryString = String.format(queryString);
            fuseki.execUpdate(queryString, "KwS.temp");
            
        }
        
//        if (true){
//            System.out.println("Gerando os caminhos....");
//            queryString = readQuery(String.format("./src/main/sparql/KwS/%1$s/kws_20_search.rq", kwsVersion));
//            queryString = String.format(queryString, servi);
//            fuseki.execUpdate(queryString, "KwS.temp");
//            
//        }

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

    private static void bkpDataset(Dataset dataset, String filename) throws FileNotFoundException, IOException {
        try (OutputStream out = new FileOutputStream(new File(filename));
                GZIPOutputStream out2 = new GZIPOutputStream(out)) {
            RDFDataMgr.write(out2, dataset, Lang.NQUADS);
            out2.finish();
            out.flush();
        } finally {
        }
    }

}
