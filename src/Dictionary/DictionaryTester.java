package Dictionary;

import XmlParser.Shared;

public class DictionaryTester {
    public static void main(String[] args) {
        Dictionary dictionary = Shared.getDictionary();
        // can test dictionary methods here
        String[] words = {"computer", "science"};
        dictionary.bm25(words);
    }
}
