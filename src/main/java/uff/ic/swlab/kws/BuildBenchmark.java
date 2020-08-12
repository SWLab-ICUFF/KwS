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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Scanner;
import java.util.zip.GZIPOutputStream;
import javax.naming.InvalidNameException;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;

/**
 *
 * @author angelo
 */
public class BuildBenchmark {

    public static void main(String[] args) throws FileNotFoundException, IOException, InvalidNameException, InterruptedException {
        String database = "BSBM_10M";
        String serviceDatabase = String.format("http://semanticweb.inf.puc-rio.br:3030/%1$s/sparql", database);
        String service2 = "http://semanticweb.inf.puc-rio.br:3030/KwS.temp/sparql";

        String kwsVersion = "v5/1/1";
        String benchmark = "IS";
        String path_database = "BSBM_10M";

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
                    run(kwsVersion, serviceDatabase, service2, keywordQuery, benchmarkNS, benchmarkFilename, database);
                }
            }

        }

    }

    public static void run(String kwsVersion, String serviceDatabase, String service2, String keywordQuery, String benchmarkNS, String filename, String database) throws FileNotFoundException, IOException, InvalidNameException, InterruptedException {
        FusekiServer fuseki = new FusekiServer("semanticweb.inf.puc-rio.br", 3030);

        String queryString = "";
        System.out.println("=============================================GERANDO BENCHMARK PARA A PALVRA CHAVE " + keywordQuery + "=============================================");

        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_00_prepare.rq", kwsVersion));
        fuseki.execUpdate(queryString, "KwS.temp");

        Calendar t1 = Calendar.getInstance();

        System.out.println("Buscando seeds");

        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_01_search_seeds.rq", kwsVersion));
        queryString = String.format(queryString, serviceDatabase, keywordQuery);
        fuseki.execUpdate(queryString, "KwS.temp");

        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_02_search_literais.rq", kwsVersion));
        queryString = String.format(queryString, serviceDatabase, keywordQuery);
        fuseki.execUpdate(queryString, "KwS.temp");

        String format_keywordQuery = keywordQuery.replaceAll("\\(|\\)", "");
        //inicio do processo de busca novas seeds
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_03_search_new_seeds.rq", kwsVersion));
        queryString = String.format(queryString, format_keywordQuery);

        QueryExecution q = QueryExecutionFactory.sparqlService(service2, queryString);
        ResultSet result = q.execSelect();
        QuerySolution soln = result.nextSolution();

        String keywordsNotSearch = String.valueOf(soln.get("new_kws"));
        //indica que existem palavras chaves que precisam ser encontradas
        if (!keywordsNotSearch.equals("")) {
            String AllKeywordsNotSearch = keywordsNotSearch;
            while (!keywordsNotSearch.equals("")) {
                
                queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_04_search_new_seed.rq", kwsVersion));
                queryString = String.format(queryString, serviceDatabase, keywordsNotSearch);
                fuseki.execUpdate(queryString, "KwS.temp");

                // verificando se inseriu alguma nova seed
                queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_03_search_new_seeds.rq", kwsVersion));
                queryString = String.format(queryString, format_keywordQuery);

                QueryExecution new_q = QueryExecutionFactory.sparqlService(service2, queryString);
                ResultSet new_result = new_q.execSelect();
                QuerySolution new_soln = new_result.nextSolution();

                String NewkeywordsNotSearch = String.valueOf(soln.get("new_kws"));
                new_q.close();
                if (NewkeywordsNotSearch.equals(AllKeywordsNotSearch)) {
                    //significa que não inseriu nenhuma palavra chave
                    String[] vector = keywordsNotSearch.split(" ");
                    ArrayList<String> listWords = new ArrayList<>();
                    if (vector.length > 1) {
                        for (int i = 1; i < vector.length; i++) {
                            listWords.add(vector[i]);
                        }

                        String searchKws = String.join(" ", listWords);
                        keywordsNotSearch = searchKws;
                    } else {
                        keywordsNotSearch = "";
                    }

                } else {
                    // inserindo literais das novas seeds
                    queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_02_search_literais.rq", kwsVersion));
                    queryString = String.format(queryString, serviceDatabase, keywordQuery);
                    fuseki.execUpdate(queryString, "KwS.temp");
                    
                    //verificando novas palavras chaves
                    queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_03_search_new_seeds.rq", kwsVersion));
                    queryString = String.format(queryString, format_keywordQuery);

                    new_q = QueryExecutionFactory.sparqlService(service2, queryString);
                    new_result = new_q.execSelect();
                    new_soln = new_result.nextSolution();
                    
                    NewkeywordsNotSearch = String.valueOf(soln.get("new_kws"));

                    new_q.close();
                    

                }

            }

        }


        System.out.println("Buscando propriedades que match");
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_05_search_properties.rq", kwsVersion));
        queryString = String.format(queryString, serviceDatabase);
        fuseki.execUpdate(queryString, "KwS.temp");

        System.out.println("Gerando os pares, calculando scores");
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_06_search.rq", kwsVersion));
        queryString = String.format(queryString, format_keywordQuery, benchmarkNS);
        fuseki.execUpdate(queryString, "KwS.temp");

        System.out.println("Filtrando o conjunto de soluções");
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_07_filter.rq", kwsVersion));
        queryString = String.format(queryString);
        fuseki.execUpdate(queryString, "KwS.temp");

        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_08_paths_0.rq", kwsVersion));
        queryString = String.format(queryString, serviceDatabase);
        fuseki.execUpdate(queryString, "KwS.temp");

        System.out.println("Gerando os caminhos de distância 1....");
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_08_paths_1.rq", kwsVersion));
        queryString = String.format(queryString, serviceDatabase);
        fuseki.execUpdate(queryString, "KwS.temp");

        System.out.println("Gerando os caminhos de distância 2....");
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_08_paths_2.rq", kwsVersion));
        queryString = String.format(queryString, serviceDatabase, service2);
        fuseki.execUpdate(queryString, "KwS.temp");

        System.out.println("Gerando os caminhos de distância 3....");
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_08_paths_3.rq", kwsVersion));
        queryString = String.format(queryString, serviceDatabase, service2);
        fuseki.execUpdate(queryString, "KwS.temp");

        System.out.println("Gerando os caminhos de distância 4....");
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_08_paths_4.rq", kwsVersion));
        queryString = String.format(queryString, serviceDatabase, service2);
        fuseki.execUpdate(queryString, "KwS.temp");

        System.out.println("Deletando grafo de pares....");
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/delete_pairs.rq", kwsVersion));
        queryString = String.format(queryString);
        fuseki.execUpdate(queryString, "KwS.temp");

        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_09_split_graph.rq", kwsVersion));
        queryString = String.format(queryString);
        fuseki.execUpdate(queryString, "KwS.temp");

        System.out.println("Calculando o score das soluções...");
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_10_search.rq", kwsVersion));
        queryString = String.format(queryString, keywordQuery, serviceDatabase, service2, format_keywordQuery, "KwS.temp");
        fuseki.execUpdate(queryString, "KwS.temp");

        System.out.println("Calculando o score final..."); //ok
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_finish.rq", kwsVersion));
        queryString = String.format(queryString, "KwS.temp");
        fuseki.execUpdate(queryString, "KwS.temp");

        Calendar t2 = Calendar.getInstance();
        double seconds = Duration.between(t1.toInstant(), t2.toInstant()).toMillis() / 1000.0;
        System.out.println("");
        System.out.println(String.format("Elapsed time: %1$f seconds", seconds));

        System.out.println("Exportando Resultado..."); //ok
        queryString = readQuery(String.format("./src/main/resources/sparql/KwS/%1$s/kws_finish_export.rq", kwsVersion));
        queryString = String.format(queryString, benchmarkNS, keywordQuery, seconds);
        fuseki.execUpdate(queryString, "KwS.temp");

        Dataset dataset = fuseki.getDataset("KwS.temp");
        bkpDataset(dataset, filename);

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
