package Dictionary;

import XmlParser.Shared;

public class DictionaryTester {
    public static Dictionary dictionary = Shared.getDictionary();

    public static void main(String[] args) {
//      Can test dictionary methods here
        getResults("Democrats' welfare and healthcare reform policies");
        getResults("Drug company bankruptcies");
        getResults("George Bush");

    }

    public static void getResults(String query) {
        System.out.println("Searching for: " + query + "\n");
        System.out.println(dictionary.weightedQuery(query));
        for (int i = 0; i < 60; i++) {
            System.out.print("*");
        }
        System.out.println("\n");
    }
}
