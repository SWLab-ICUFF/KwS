/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uff.ic.swlab.util.recall.coffman.datasets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author angelo
 */
public class BuildCSVSearchID {

    public static ArrayList<String> GetTableNames(Connection conn, String database) throws ClassNotFoundException {
        ArrayList<String> allTablesList = new ArrayList<>();

        String queryString = String.format("SELECT table_name FROM information_schema.tables WHERE table_catalog = '%1$s' and table_schema = 'public'", database);
        try {
            Statement statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(queryString);
            while (resultSet.next()) {
                String tableName = resultSet.getString("table_name");
                allTablesList.add(tableName);
            }
        } catch (SQLException ex) {
            Logger.getLogger(BuildCSVSearchID.class.getName()).log(Level.SEVERE, null, ex);
        }

        return allTablesList;

    }

    public static ArrayList<Integer> getSearchIds(String line) {
        ArrayList<Integer> listSearchId = new ArrayList<>();
        Pattern pattern = Pattern.compile("\\d+");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            Integer search_id = Integer.parseInt(matcher.group());
            if (!listSearchId.contains(search_id))
                listSearchId.add(search_id);

        }

        return listSearchId;
    }

    public static HashMap<Integer, ArrayList<String>> getNamebySearchId(ArrayList<Integer> listIds,
            Connection conn, ArrayList<String> allTableslist, Integer keywordNumber, ArrayList<String> colunsList) {
        HashMap<Integer, ArrayList<String>> mapSearchId = new HashMap<>();
        //achar name para cada seach_id da lista
        ArrayList<String> listNames = new ArrayList<>();
        for (Integer searchId : listIds)
            for (String table_name : allTableslist)
                try {
                for (String column_name : colunsList)
                        try {
                    Statement statement = conn.createStatement();
                    String queryString = String.format("SELECT %3$s FROM %1$s WHERE __search_id = '%2$s'", table_name, searchId, column_name);
                    ResultSet resultSet = statement.executeQuery(queryString);
                    while (resultSet.next()) {
                        String name = resultSet.getString(column_name).replace(",", "");
                        listNames.add(name);
                        break; //proximo searchid
                    }
                } catch (Throwable e) {
                    continue; //coninua para proxima coluna
                }

            } catch (Throwable e) {
                continue; //proximo searchid
            }
        mapSearchId.put(keywordNumber, listNames);
        return mapSearchId;

    }

    public static void ExportCSV(ArrayList<HashMap<Integer, ArrayList<String>>> listMapkeywords, String nameDatabase) throws FileNotFoundException, IOException {

        FileWriter csvWriter = new FileWriter(String.format("./src/main/resources/benchmarks/Recall/Results/%1$s_search_id.csv", nameDatabase));
        for (HashMap<Integer, ArrayList<String>> mapSearchId : listMapkeywords)
            for (Integer keywordNumber : mapSearchId.keySet()) {
                ArrayList<String> listNames = mapSearchId.get(keywordNumber);
                csvWriter.append(keywordNumber.toString());
                csvWriter.append(";");
                csvWriter.append(String.join(",", listNames));
                csvWriter.append("\n");
            }
        csvWriter.flush();
        csvWriter.close();

//        File folder = new File(String.format("./src/main/resources/benchmarks/Recall/Results/%1$s_search_id.csv", nameDatabase));
//        try ( PrintWriter writer = new PrintWriter(folder)) {
//            StringBuilder sb = new StringBuilder();
//            for (HashMap<Integer, ArrayList<String>> mapSearchId : listMapkeywords) {
//                for (Integer keywordNumber : mapSearchId.keySet()) {
//                    ArrayList<String> listNames = mapSearchId.get(keywordNumber);
//                    sb.append(keywordNumber);
//                    sb.append(';');
//                    sb.append(listNames);
//                    sb.append('\n');
//
//                }
//            }
//
//            writer.write(sb.toString());
//
//        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException, FileNotFoundException, IOException {
        //entrada

        //IMDb
//        String pathName = "IMDb";
//        String databaseName = "IMDbCoffman";
//        ArrayList<String> colunsList = new ArrayList<>();
//        colunsList.add("name");
//        colunsList.add("title");
        //Mondial
        String pathName = "Mondial";
        String databaseName = "MondialCoffman";
        ArrayList<String> colunsList = new ArrayList<>();
        colunsList.add("name");

        ArrayList<HashMap<Integer, ArrayList<String>>> listMapkeywords = new ArrayList<>();
        ConnectionPostgres connObject = new ConnectionPostgres();
        Connection conn = connObject.connect();

        ArrayList<String> allTableList = GetTableNames(conn, databaseName);

        File folder = new File(String.format("./src/main/resources/benchmarks/Recall/answers_coffman/%1$s", pathName));
        File[] listOfFiles = folder.listFiles();
        Arrays.sort(listOfFiles);

        for (int i = 0; i < listOfFiles.length; i++) {
            ArrayList<Integer> allsearchids = new ArrayList<>();
            File file = listOfFiles[i];
            Integer KeywordNumber = Integer.parseInt(file.getName().replace(".txt", ""));
            try (InputStream in = new FileInputStream(file); Scanner sc = new Scanner(in)) {
                while (sc.hasNext()) {
                    String line = sc.nextLine().trim();
                    if (line != null && !line.equals("") && line.startsWith("([")) {
                        ArrayList<Integer> listids = getSearchIds(line);
                        for (Integer searchId : listids)
                            if (!allsearchids.contains(searchId))
                                allsearchids.add(searchId);

                    }
                }

            }
            HashMap<Integer, ArrayList<String>> mapSearchId = getNamebySearchId(allsearchids, conn, allTableList, KeywordNumber, colunsList);
            listMapkeywords.add(mapSearchId);

        }
        ExportCSV(listMapkeywords, pathName);
        conn.close();

    }

}
