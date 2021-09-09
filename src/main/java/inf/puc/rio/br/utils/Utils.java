/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.utils;

import inf.puc.rio.br.Model.Constants;
import inf.puc.rio.br.Model.Entity;
import inf.puc.rio.br.Model.KeywordQuery;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.rdfconnection.RDFConnectionFactory;

/**
 *
 * @author angelo
 */
public class Utils {

    public static boolean findNode(ArrayList<ArrayList<Integer>> nodesRelation, Integer v, Integer j) {
        boolean result = false;

        for (ArrayList<Integer> nodesVisited : nodesRelation) {
            int vVisited = nodesVisited.get(0);
            int jVisited = nodesVisited.get(1);

            if (vVisited == v && jVisited == j) {
                return true;
            }
        }

        return result;

    }

    public static ArrayList<String> getSetNodesNeighborhood() throws IOException {
        ArrayList<String> listEntity = new ArrayList<>();

        String queryString = Utils.readQuery(String.format("%1$s/getEntities.rq", Constants.pathSparlNeighborhood));
        QueryExecution q = QueryExecutionFactory.sparqlService(Constants.serviceKwSTemp, queryString);
        ResultSet result = q.execSelect();

        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();

            String uriEntity = soln.get("entity").toString();
            listEntity.add(uriEntity);

        }
        q.close();
        return listEntity;
    }

    public static void ExportResults(ArrayList<KeywordQuery> listKw) throws FileNotFoundException, UnsupportedEncodingException {

        PrintWriter writerLabel = new PrintWriter(Constants.pathResults + Constants.database + "_keywordqueries.txt", "UTF-8");

        HashMap<String, Set<String>> mapLabels = new HashMap<>();

        for (KeywordQuery kw : listKw) {
            ArrayList<inf.puc.rio.br.Model.Pattern> listPattern = kw.getListPatterns();

            for (inf.puc.rio.br.Model.Pattern pattern : listPattern) {
                String patternString = pattern.getPattern();

                if (!mapLabels.containsKey(patternString)) {
                    mapLabels.put(patternString, pattern.getKeywordLabel());
                } else {
                    Set<String> setInstances = mapLabels.get(patternString);
                    setInstances.addAll(pattern.getKeywordLabel());

                }

            }
        }
        writerLabel.println("\n");
        writerLabel.println("Total de Patterns:" + mapLabels.size());
        for (Map.Entry<String, Set<String>> entry : mapLabels.entrySet()) {
            String pattern = entry.getKey();
            Set<String> keywordsSet = entry.getValue();

            writerLabel.println("\n");
            writerLabel.println("PATTERN:");
            writerLabel.println(pattern);
            writerLabel.println("\n");

            for (String keywords : keywordsSet) {
                writerLabel.println(String.join("\n", keywords));
            }

        }
        writerLabel.close();

    }

    public static HashMap<String, String> getClassLabels() throws IOException {
        HashMap<String, String> mapClassLabels = new HashMap<>();

        String queryString = Utils.readQuery(String.format("%1$s/get_class_labels.rq", Constants.pathSparqlPreProcessing));
        QueryExecution q = QueryExecutionFactory.sparqlService(Constants.serviceDatabase, queryString);
        ResultSet result = q.execSelect();

        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();

            String classURI = soln.get("class").toString();
            String label = soln.get("label").toString();
            mapClassLabels.put(classURI, label);
        }
        q.close();
        return mapClassLabels;
    }

    public static HashMap<String, String> getPropertiesLabels() throws IOException {
        HashMap<String, String> mapPropertiesLabels = new HashMap<>();

        String queryString = Utils.readQuery(String.format("%1$s/get_properties_labels.rq", Constants.pathSparqlPreProcessing));
        QueryExecution q = QueryExecutionFactory.sparqlService(Constants.serviceDatabase, queryString);
        ResultSet result = q.execSelect();

        while (result.hasNext()) {
            QuerySolution soln = result.nextSolution();

            String propertyURI = soln.get("property").toString();
            String label = soln.get("label").toString();
            mapPropertiesLabels.put(propertyURI, label);
        }
        q.close();

        return mapPropertiesLabels;

    }

    public static Model createModelFromTriples(ArrayList<String> listTriple) {
        Model model = ModelFactory.createDefaultModel();
        for (String triple : listTriple) {
            String[] vTriple = triple.split(" ");

            String subject = vTriple[0];
            String predicate = vTriple[1];
            String object = vTriple[2];

            Property p = model.createProperty(predicate);
            Resource resource = model.createResource(subject);

            Node node = NodeFactory.createURI(object);
            RDFNode nodeRDF = model.asRDFNode(node);

            model.add(resource, p, nodeRDF);

        }

        return model;
    }

    public static Set<Set<String>> filterPowerSetbyClass(Set<Set<String>> powerSet, String nodeFilter) {
        Set<Set<String>> powerSetFilter = new HashSet<>();

        for (Set<String> set : powerSet) {
            if (set.contains(nodeFilter)) {
                powerSetFilter.add(set);
            }
        }

        return powerSetFilter;
    }

    public static Set<Set<String>> filterSizeElementsPowerSet(Set<Set<String>> powerSet) {
        Set<Set<String>> powerSetFilter = new HashSet<>();

        for (Set<String> set : powerSet) {
            if ((set.size() > 0) && (set.size() <= 3)) {
                powerSetFilter.add(set);
            }
        }

        return powerSetFilter;

    }

    public static Set<Set<String>> filterPairMirrorPowerSet(Set<Set<String>> powerSet) {
        Set<Set<String>> powerSetFilter = new HashSet<>();

        for (Set<String> set : powerSet) {
            int countMirrors = 0;

            for (String element : set) {
                if (element.contains("_Mirror_")) {
                    countMirrors++;
                }

            }
            if ((countMirrors == 1)) {
                powerSetFilter.add(set);
            }

        }

        return powerSetFilter;

    }
    
//    public static Set<Set<String>> powerSet(Set<String> elements)
//    {
//        // convert set to a list
//        List<String> S = new ArrayList<>(elements);
// 
//        // `N` stores the total number of subsets
//        //long N = (long) Math.pow(2, S.size());
//        long N = 10000000;
//        
//        // Set to store subsets
//        Set<Set<String>> result = new HashSet<>();
// 
//        // generate each subset one by one
//        for (int i = 0; i < N; i++)
//        {
//            Set<String> set = new HashSet<>();
// 
//            // check every bit of `i`
//            //max 2ˆ4
//            for (int j = 0; j < 3; j++)
//            {
//                // if j'th bit of `i` is set, add `S[j]` to the current set
//                if ((i & (1 << j)) != 0) {
//                    set.add(S.get(j));
//                }
//            }
//            result.add(set);
//        }
// 
//        return result;
//    }


    //see: https://stackoverflow.com/questions/1670862/obtaining-a-powerset-of-a-set-in-java
    public static <T> Set<Set<T>> powerSet(Set<T> originalSet, int maxSizeSet) {
        Set<Set<T>> sets = new HashSet<Set<T>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<T>());
            return sets;
        }
        List<T> list = new ArrayList<T>(originalSet);
        T head = list.get(0);
        Set<T> rest = new HashSet<T>(list.subList(1, list.size()));

        for (Set<T> set : powerSet(rest, maxSizeSet)) {
            if (set.size() <= maxSizeSet - 1) {
            Set<T> newSet = new HashSet<T>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
           }

        }

        return sets;
    }

    public static Set<String> getElementsFromModel(Model schemaModel) {
        Set<String> elements = new HashSet<>();

        StmtIterator modelIterator = schemaModel.listStatements();

        while (modelIterator.hasNext()) {
            Statement stmt = modelIterator.nextStatement();

            String subject = stmt.getSubject().toString();
            String object = stmt.getObject().toString();

            elements.add(subject);
            elements.add(object);
        }

        return elements;

    }

    public static Model createMirrorFromGraph(int sizeMirrors, String serviceDatabase, String graphCutOutSchema) {

        Model cutOutSchema = RDFConnectionFactory.connectFuseki(serviceDatabase).fetch(graphCutOutSchema);

        Model schemaModel = ModelFactory.createDefaultModel();

        //criando as classes espelhos
        Set<String> nodeSet = new HashSet<>();
        HashMap<String, ArrayList<String>> mapMirrosSubject = new HashMap<>();
        HashMap<String, ArrayList<String>> mapMirrosObject = new HashMap<>();

        StmtIterator itercutOutSchema = cutOutSchema.listStatements();

        //criando os nodes
        while (itercutOutSchema.hasNext()) {
            Statement stmt = itercutOutSchema.nextStatement();

            String subject = stmt.getSubject().toString();
            String object = stmt.getObject().toString();

            nodeSet.add(subject);
            nodeSet.add(object);

            ArrayList<String> listMirrorSubject = new ArrayList<>();
            ArrayList<String> listMirrorObject = new ArrayList<>();
            for (int i = 0; i < sizeMirrors; i++) {
                String uriSubjectNode = subject + "_Mirror_" + i;
                String uriObjectNode = object + "_Mirror_" + i;

                listMirrorSubject.add(uriSubjectNode);
                listMirrorObject.add(uriObjectNode);
            }
            if (!mapMirrosSubject.containsKey(subject)) {
                mapMirrosSubject.put(subject, listMirrorSubject);
            }

            if (!mapMirrosObject.containsKey(object)) {
                mapMirrosObject.put(object, listMirrorObject);
            }

        }

        //criar relacoes
        for (String nodeURI : nodeSet) {
            Resource resourceNode = cutOutSchema.getResource(nodeURI);

            StmtIterator edgesList = resourceNode.listProperties();

            ArrayList<String> classMirrorSubject = mapMirrosSubject.get(resourceNode.toString());

            while (edgesList.hasNext()) {

                Statement stmt = edgesList.nextStatement();

                Property edge = stmt.getPredicate();
                RDFNode object = stmt.getObject();

                ArrayList<String> classMirrorObject = mapMirrosObject.get(object.toString());

                for (String uriMirror : classMirrorSubject) {

                    Resource resourceNodeMirror = schemaModel.createResource(uriMirror);

                    for (String uriMirrorObject : classMirrorObject) {
                        Resource objectMirror = schemaModel.createResource(uriMirrorObject);

                        schemaModel.add(resourceNodeMirror, edge, objectMirror);
                        schemaModel.add(resourceNodeMirror, edge, object);
                        schemaModel.add(resourceNode, edge, objectMirror);
                    }

                }
            }

        }

        //equivalente a: schemaModel.add(resourceNode, edge, object);
        schemaModel.add(cutOutSchema);

        return schemaModel;

    }

    public static String readQuery(String filename) throws FileNotFoundException, IOException {
        File file = new File(filename);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }
        return new String(data, "UTF-8");
    }

    public static void printTriples(Model model) {
        StmtIterator iter = model.listStatements();

        while (iter.hasNext()) {
            Statement stmt = iter.nextStatement();
            Resource subject = stmt.getSubject();
            Property predicate = stmt.getPredicate();
            RDFNode object = stmt.getObject();

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
                System.out.print(object.toString());
            } else {
                System.out.print(" \"" + object.toString() + "\"");
            }
            System.out.println(" .");
        }
    }

    public static String preparedTriples(ArrayList<String> listTriple) {
        return String.join(". ", listTriple);
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public static String getRandomElement(String[] array, ArrayList elementsNotSort) {
        boolean loop = true;
        String element = null;
        while (loop) {
            int rnd = new Random().nextInt(array.length);
            element = array[rnd];

            int find = 0;
            if (!elementsNotSort.contains(element)) {
                loop = false;
            }

        }

        return element;

    }

    public static ArrayList<String> ExtractWord(String text) {
        text = text.replace("'", "").replace("\'", "");
        ArrayList<String> words = new ArrayList<>();
        Pattern p = Pattern.compile("[a-zA-Z]+");
        Matcher match = p.matcher(text);

        while (match.find()) {
            words.add(match.group());
        }

        return words;

    }

    public static ArrayList<String> removeAcento(ArrayList<String> words) {
        ArrayList<String> newList = new ArrayList<>();
        for (String word : words) {
            String newWord = Normalizer.normalize(word, Normalizer.Form.NFD);
            newWord = newWord.replace("'", "").replace("\'", "");
            newWord = newWord.replaceAll("[^\\p{ASCII}]", "");
            newList.add(newWord);

        }
        return newList;
    }

    public static boolean isDate(Literal lit) {
        try {
            String dateString = lit.getString();
            ArrayList<SimpleDateFormat> listFormat = new ArrayList<>();

            SimpleDateFormat formatter1 = new SimpleDateFormat("dd/MM/yyyy");
            listFormat.add(formatter1);
            SimpleDateFormat formatter2 = new SimpleDateFormat("dd-MMM-yyyy");
            listFormat.add(formatter2);
            SimpleDateFormat formatter3 = new SimpleDateFormat("yyyy-MM-dd");
            listFormat.add(formatter3);
            SimpleDateFormat formatter4 = new SimpleDateFormat("MM dd, yyyy");
            listFormat.add(formatter4);
            SimpleDateFormat formatter5 = new SimpleDateFormat("E, MMM dd yyyy");
            listFormat.add(formatter5);
            SimpleDateFormat formatter6 = new SimpleDateFormat("E, MMM dd yyyy HH:mm:ss");
            listFormat.add(formatter6);
            SimpleDateFormat formatter7 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
            listFormat.add(formatter7);

            int flag = 0;
            for (SimpleDateFormat format : listFormat) {
                try {
                    Date date = format.parse(dateString);
                    flag = 1;
                    break;
                } catch (Throwable e) {
                    continue;
                }
            }
            if (flag == 1) {
                return true;
            }

            return false;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean isNumberDouble(Literal lit) {
        try {
            lit.getDouble();
            return true;
        } catch (Throwable e) {
            return false;
        }

    }

    public static boolean isNumberFloat(Literal lit) {
        try {
            lit.getFloat();
            return true;
        } catch (Throwable e) {
            return false;
        }

    }

    public static boolean isNumberInteger(Literal lit) {
        try {

            Integer.parseInt(String.valueOf(lit.getString()));
            return true;
        } catch (Throwable e) {
            return false;
        }

    }

    public static boolean isBoolean(Literal lit) {
        try {
            lit.getBoolean();
            return true;
        } catch (Throwable e) {
            return false;
        }

    }

    public static boolean isDataType(Literal lit) {
        try {
            lit.getDatatype();
            return true;
        } catch (Throwable e) {
            return false;
        }

    }

    public static Entity findEntity(ArrayList<Entity> allEntities, String uri) {

        for (Entity entity : allEntities) {
            if (entity.getUriEntity().equals(uri)) {
                return entity;
            }
        }

        return null;
    }

}
