/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.Model;

/**
 *
 * @author angelo
 */
public class Constants {

    //public static final String database = "Mondial_KeywordQuery";
    public static final String database = "BSBM_1M";

    public static final String pathStopWord = "./src/main/resources/config/english_stopwords.txt";
    public static final String pathSparqlPreProcessing = "./src/main/resources/scripts/sparql/pre_processing/";

    public static final String pathSparlPreparation = "./src/main/resources/scripts/sparql/step0/";
    public static final String pathSparlInducers = "./src/main/resources/scripts/sparql/step1/";
    public static final String pathSparlNeighborhood = "./src/main/resources/scripts/sparql/step2/";
    public static final String createSchema = "./src/main/resources/scripts/sparql/step3/";
    public static final String pathSparlSTs = "./src/main/resources/scripts/sparql/step4/";
    public static final String pathSparlInstance = "./src/main/resources/scripts/sparql/step5/";
    public static final String pathSparlExtractKeywords = "./src/main/resources/scripts/sparql/step6/";

    public static final String pathResults = String.format("./src/main/resources/results/%1$s/", database);

    public static final String serviceDatabase = String.format("http://semanticweb.inf.puc-rio.br:3030/%1$s/sparql", database);
    public static final String serviceKwSTemp = "http://semanticweb.inf.puc-rio.br:3030/KwS.temp/sparql";
    public static final String serviceKwSTempData = "http://semanticweb.inf.puc-rio.br:3030/KwS.temp/data";

}
