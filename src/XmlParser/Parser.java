package XmlParser;

import Dictionary.Dictionary;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Parser implements Serializable {

    private final int amountOfFiles = 21578;

    public static void main(String[] args) {
        Parser parser = new Parser();
        parser.quickParse();
    }

    // This is used if it is not necessary to split the dictionary into blocks. Quickly parses documents and stores
    // them into quickly_parsed_dictionary
    public void quickParse() {
        long startTime = System.nanoTime();

        Dictionary dictionary = new Dictionary();
        DocumentFetcher fetcher = new DocumentFetcher();

        for (int i = 1; i <= Shared.NUMBER_OF_DOCUMENTS; i++) {
            System.out.printf("%-20s%8s%n", "Indexing document: ", i + "/" + amountOfFiles);
            ArrayList<String> documentTokens = fetcher.getTokens(i);
            if (Shared.STEMMED) {
                documentTokens = porterStem(documentTokens);
            }
            for (String token: documentTokens) {
                dictionary.submitWord(token, i);
            }
        }
        System.out.println("\nWriting dictionary to disk...\n");
        if (Shared.STEMMED) {
            String path = "/stemmed/dictionary";
            saveDictionary(dictionary, path);
        } else {
            String path = "/ordinary/dictionary";
            saveDictionary(dictionary, path);
        }

        long timeToComplete = (System.nanoTime()-startTime)/1000000000;
        System.out.println("Creating and writing all blocks to disk took " + timeToComplete + " seconds.");
    }

    // This is a slower merge. Used if the memory of dictionary exceeds the memory of the machine.
    public void mergeParse() {
        int blockSize = 5500;

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
                if (Shared.STEMMED) {
                    documentTokens = porterStem(documentTokens);
                }
                for (String token: documentTokens) {
                    dictionary.submitWord(token, documentCount);
                }
            }
            System.out.println("\nWriting block " + blockCount + " to disk...\n");
            if (Shared.STEMMED) {
                String path = "/stemmed/blocks/block_" + blockCount++;
                saveDictionary(dictionary, path);
            } else {
                String path = "/ordinary/blocks/block_" + blockCount++;
                saveDictionary(dictionary, path);
            }
        }

        Merger merger = new Merger();
        merger.merge();

        long timeToComplete = (System.nanoTime()-startTime)/1000000000;
        System.out.println("Creating and writing all blocks to disk took " + timeToComplete + " seconds.");
    }

    // This is used if having a small dictionary is of utmost importance.
    // Provides lossy compression by only saving the stem of words. Ex: operation, operative, etc => operat
    public ArrayList<String> porterStem(List<String> tokens1){
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

    // Writes the dictionary to disk
    public void saveDictionary(Dictionary dictionary, String fileName) {
        try {
            String path = Shared.DICTIONARY_PATH + fileName;
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path));
            outputStream.writeObject(dictionary);
            outputStream.close();
            System.out.println("Successfully saved your dictionary: " + fileName);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
