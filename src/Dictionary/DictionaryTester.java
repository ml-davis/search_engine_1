package Dictionary;

import XmlParser.Shared;

public class DictionaryTester {
    public static void main(String[] args) {
//      Can test dictionary methods here
        Dictionary dictionary = Shared.getDictionary();

//        Democrats' welfare and healthcare reform policies
        String[] words = {"democrats", "welfare", "and", "healthcare", "reform", "policies"};
        dictionary.bm25(words);

    }
}
