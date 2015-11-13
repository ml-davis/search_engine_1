package XmlParser;

import Dictionary.Dictionary;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class Shared {
    public static final String DICTIONARY_PATH = "/home/matthew/SearchEngine/Dictionaries/";
    public static final String XML_FILE_PATH = "/home/matthew/SearchEngine/Reuters/xml_files/";
    public static final Boolean STEMMED = false;

    public static final double NUMBER_OF_WORDS = 2757710; // this is total amount of words (not unique amount of words)
    public static final double NUMBER_OF_DOCUMENTS = 21578;
    public static final double AVERAGE_DOCUMENT_LENGTH = NUMBER_OF_WORDS/NUMBER_OF_DOCUMENTS;

    public static Dictionary getDictionary() {
        Dictionary d = null;
        long startTime = System.nanoTime();
        try {
            String path;
            if (Shared.STEMMED) {
                path = Shared.DICTIONARY_PATH + "stemmed/merged";
            } else {
                path = Shared.DICTIONARY_PATH + "ordinary/merged";
            }
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(path));
            d = (Dictionary) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        long duration = (endTime-startTime)/1000000000;
        System.out.println("Time to get dictionary: " + duration + " seconds");
        return d;
    }

    public static String filterString(String document) {
        document = document.replaceAll("\\n", " ");
        document = document.replaceAll("[^a-zA-Z']", " ");
        document = document.replaceAll("\\b.\\b", " ");
        document = document.replaceAll(" '.+ ", " ");
        document = document.toLowerCase();
        return document;
    }
}
