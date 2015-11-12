package Dictionary;

import XmlParser.Shared;

public class DictionaryTester {
    public static void main(String[] args) {
//      Can test dictionary methods here
        Dictionary dictionary = Shared.getDictionary();

        String[] words = {"whittaker", "restructurining"};
        dictionary.bm25(words);

    }
}
