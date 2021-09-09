/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package inf.puc.rio.br.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author angelo
 */
public class RemoveStopWords {

    private List<String> stopwords;
    private String pathFile;

    public RemoveStopWords(String pathFile) throws IOException {
        this.pathFile = pathFile;
        loadFileStopWords();

    }

    public void loadFileStopWords() throws FileNotFoundException, IOException {
        //code: https://stackoverflow.com/questions/12469332/how-to-remove-stop-words-in-java
        stopwords = Files.readAllLines(Paths.get(pathFile));
    }

    public ArrayList<String> deletStopWord(ArrayList<String> allWords) {
        // ArrayList<String> allWords = Stream.of(original.toLowerCase().split(" "))
        //     .collect(Collectors.toCollection(ArrayList<String>::new));

        allWords.removeAll(stopwords);

        //String result = allWords.stream().collect(Collectors.joining(" "));
        return allWords;
    }

}
