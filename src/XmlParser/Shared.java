package XmlParser;

import Dictionary.Dictionary;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

// Provides methods that are used between several
public class Shared {

    // Project Options
    public static final boolean STEMMED = true;
    public static final boolean QUICK_PARSE = false;

    // paths
    public static final String DICTIONARY_PATH = "/home/matthew/SearchEngine/Dictionaries/";
    public static final String SGM_FILE_PATH = "/home/matthew/SearchEngine/Reuters/sgm_files/";
    public static final String XML_FILE_PATH = "/home/matthew/SearchEngine/Reuters/xml_files/";

    // constants
    // this is total amount of words that appear in all documents (not total unique)
//    public static final double NUMBER_OF_WORDS = 2757710;
    public static final double NUMBER_OF_WORDS = 3400215;
    public static final double NUMBER_OF_DOCUMENTS = 21578;
    public static final double AVERAGE_DOCUMENT_LENGTH = NUMBER_OF_WORDS/NUMBER_OF_DOCUMENTS;

    // Returns the dictionary dependent on the specified options above
    public static Dictionary getDictionary() {
        Dictionary d = null;
        long startTime = System.nanoTime();
        try {
            String path;
            if (STEMMED) {
                path = Shared.DICTIONARY_PATH + "stemmed/dictionary";
            } else {
                path = Shared.DICTIONARY_PATH + "ordinary/dictionary";
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

    // Translates query to an array of search tokens. These tokens are filtered by regex and do not contain stop words.
    public static String[] getSearchTokens(String query) {
        query = filterString(query);
        String[] words = query.split(" ");
        words = removeStopWords(words);

        return words;
    }

    // Used in getSearchTokens
    private static String filterString(String text) {
        text = text.replaceAll("\\n", " ");
        text = text.replaceAll("[^a-zA-Z']", " ");
        text = text.replaceAll("\\b.\\b", " ");
        text = text.replaceAll(" '.+ ", " ");
        text = text.toLowerCase();
        return text;
    }

    // Used by getSearchTokens
    private static String[] removeStopWords(String[] words) {
        String[] stopWords = {"I", "a", "about", "an", "are", "as", "at", "be", "by", "for", "from", "how", "in", "is",
            "it", "of", "on", "or", "that", "the", "this", "to", "was", "what", "when", "where", "who", "will", "with",
            "the"};
        ArrayList<String> filteredWords = new ArrayList<>();
        for (String word : words) {
            boolean hasWord = false;
            for (String stopWord : stopWords) {
                if (word.equals(stopWord)) {
                    hasWord = true;
                }
            }
            if (!hasWord) {
                filteredWords.add(word);
            }
        }
        String[] results = new String[filteredWords.size()];
        for (int i = 0; i < filteredWords.size(); i++) {
            results[i] = filteredWords.get(i);
        }

        return results;
    }
}
