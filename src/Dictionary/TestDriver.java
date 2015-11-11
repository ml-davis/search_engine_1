package Dictionary;

import XmlParser.DocumentFetcher;

import java.io.*;

public class TestDriver {
    public static void main(String[] args) {

    }

    public static Dictionary getDictionary() {
        Dictionary d = null;
        long startTime = System.nanoTime();
        try {
            ObjectInputStream inputStream = new ObjectInputStream(
                    new FileInputStream("/home/matthew/SearchEngine/Dictionaries/ordinary_dictionary")
            );
            d = (Dictionary) inputStream.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        long endTime = System.nanoTime();
        long duration = (endTime-startTime)/1000000000;
        System.out.println("Time to get dictionary: " + duration + " seconds");
        return d;
    }

}
