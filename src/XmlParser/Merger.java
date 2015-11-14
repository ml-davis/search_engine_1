package XmlParser;

import Dictionary.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Set;
import java.util.Stack;

// This is used in the merge sort. Used if the dictionary is larger than memory.
public class Merger {

    public void merge() {
        String path = Shared.DICTIONARY_PATH;
        if (Shared.STEMMED) {
            path += "stemmed/";
        } else {
            path += "ordinary/";
        }

        File directory = new File(path);
        File[] dictionaryFiles = directory.listFiles();
        ArrayList<Dictionary> dictionaries = new ArrayList<>();
        Stack<Dictionary> stack = new Stack();
        if (dictionaryFiles != null) {
            for (int i = 0; i < dictionaryFiles.length; i++) {
                try {
                    System.out.println("Retrieving dictionaries...");
                    ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(dictionaryFiles[i]));
                    Dictionary dictionary = (Dictionary)inputStream.readObject();
                    dictionaries.add(dictionary);
                    System.out.println("Successfully retrieved dictionary " + (i));
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        int mergeCount = 1;

        while (dictionaries.size() > 1) {
            if (dictionaries.size() % 2 == 1) {
                stack.push(dictionaries.remove(dictionaries.size() - 1));
            }
            ArrayList<Dictionary> nextRow = new ArrayList<>();
            for (int i = 1; i < dictionaries.size(); i+=2) {
                System.out.println("Merging...");
                Dictionary d = mergeDictionaries(dictionaries.get(i), dictionaries.get(i - 1));
                System.out.println("Merge " + mergeCount++ + " complete");
                nextRow.add(d);
            }
            dictionaries = nextRow;
        }
        Dictionary d = dictionaries.get(0);
        while (!stack.isEmpty()) {
            System.out.println("Merging...");
            d = mergeDictionaries(dictionaries.get(0), stack.pop());
            System.out.println("Merge " + mergeCount++ + " complete");
        }

        System.out.println("Writing dictionary to disk");
        saveDictionary(d, path);
        System.out.println("Write to disk successful");
    }

    private Dictionary mergeDictionaries(Dictionary d1, Dictionary d2) {
        Dictionary dictionary = new Dictionary();
        Set d2KeySet = d2.getKeySet();
        for (Object key : d2KeySet) {
            TermInfo info = d2.getTermInfo((String)key);
            for (int j = 0; j < info.getDocumentsFound().size(); j++) {
                Document document = info.getDocument(j);
                int docId = document.getDocumentNumber();
                for (int k = 0; k < document.getDocumentFrequency(); k++) {
                    dictionary.submitWord((String)key, docId);
                }
            }
        }
        return dictionary;
    }

    public void saveDictionary(Dictionary dictionary, String path) {
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(path + "/merged"));
            outputStream.writeObject(dictionary);
            outputStream.close();
            System.out.println("Successfully saved your dictionary: \n" + path);

        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
