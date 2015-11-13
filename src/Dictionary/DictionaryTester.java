package Dictionary;

import XmlParser.Shared;

public class DictionaryTester {
    public static void main(String[] args) {
//      Can test dictionary methods here
//        Dictionary dictionary = Shared.getDictionary();
//        System.out.println(dictionary.weightedQuery("George Bush"));

        String[] words = {"the", "boy", "went"};
        words = Shared.removeStopWords(words);
        for (String word : words) {
            System.out.println(word);
        }
    }
}
