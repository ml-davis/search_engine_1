package XmlParser;

import Dictionary.Dictionary;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Parser implements Serializable {
    private static final String dictionaryPath = "/home/matthew/SearchEngine/Dictionaries";
    private static final int blockSize = 5500;
    private static final int amountOfFiles = 21578;
    private static boolean stemmed = true;

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.createDictionary();
    }

    public void createDictionary() {
        long startTime = System.nanoTime();

        Dictionary dictionary = new Dictionary();
        DocumentFetcher fetcher = new DocumentFetcher();

        int blockCount = 1;
        double totalBlocks = Math.ceil(amountOfFiles/blockSize);
        for (int i = 0; i <= totalBlocks; i++) {
            int start = (blockCount-1)*blockSize+1;
            int end = (blockCount*blockSize < amountOfFiles) ? blockCount*blockSize : amountOfFiles;
            for (int documentCount = start; documentCount <= end; documentCount++) {
                System.out.printf("%-20s%8s%n", "Indexing document: ", documentCount + "/" + amountOfFiles
                    + " to block " + blockCount);
                ArrayList<String> documentTokens = fetcher.getTokens(documentCount);
                if (stemmed) {
                    documentTokens = porterStem(documentTokens);
                }
                for (String token: documentTokens) {
                    dictionary.submitWord(token, documentCount);
                }
            }
            System.out.println("\nWriting block " + blockCount + " to disk...\n");
            if (stemmed) {
                String path = "/stemmed/block_" + blockCount++;
                saveDictionary(dictionary, path);
            } else {
                String path = "/ordinary/block_" + blockCount++;
                saveDictionary(dictionary, path);
            }
        }

        long timeToComplete = (System.nanoTime()-startTime)/1000000000;
        System.out.println("Creating and writing all blocks to disk took " + timeToComplete + " seconds.");
    }

    public static ArrayList<String> porterStem(List<String> tokens1){
        PorterStemmer stemmer = new PorterStemmer();
        ArrayList<String> stemmedTokens = new ArrayList<>();
        for (String token : tokens1){
            String s1 = stemmer.step1(token);
            String s2 = stemmer.step2(s1);
            String s3= stemmer.step3(s2);
            String s4= stemmer.step4(s3);
            String s5= stemmer.step5(s4);
            stemmedTokens.add(s5);
        }
        return stemmedTokens;
    }

    public void saveDictionary(Dictionary dictionary, String fileName) {
        try {
            String path = dictionaryPath + fileName;
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path));
            outputStream.writeObject(dictionary);
            outputStream.close();
            System.out.println("Successfully saved your dictionary: " + fileName);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
