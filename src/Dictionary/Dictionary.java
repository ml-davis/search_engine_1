package Dictionary;

import XmlParser.DocumentFetcher;
import XmlParser.Shared;

import java.io.Serializable;
import java.util.*;

public class Dictionary implements Serializable {
    private int totalWordCount;
    private HashMap<String, TermInfo> dictionary;

    // Constructor
    public Dictionary() {
        this.totalWordCount = 0;
        this.dictionary = new HashMap<>();
    }

    // Submits a word to the ADT
    public void submitWord(String word, int documentNumber) {
        if (!dictionary.containsKey(word)) {
            totalWordCount++;
            dictionary.put(word, new TermInfo(documentNumber));
        } else {
            dictionary.get(word).addTermOccurrence(documentNumber);
        }
    }

    // Get a string that contains the argument words info in the dictionary
    public String getWord(String word) {
        if (dictionary.containsKey(word)) {
            return word + dictionary.get(word);
        } else {
            return "Word not found";
        }
    }

    // Gives the ability browse the contents of dictionary. Returns the words in specified range.
    public void showDictionary(int start, int end) {
        ArrayList<String> sortedDictionary = new ArrayList<>(dictionary.keySet());
        System.out.println("Sorting dictionary...");
        Collections.sort(sortedDictionary);
        System.out.println("Printing term " + start + " to " + end);
        for (int i = start; i < end; i++) {
            System.out.println(sortedDictionary.get(i));
        }
    }

    public String intersectionQuery(String query) {
        String[] words = Shared.getSearchTokens(query);
        if (words.length > 1) {
            // must find the intersection of all documents that contain all search terms

            // collect all the lists of documents for each term
            ArrayList<ArrayList<Document>> documents = new ArrayList<>();
            for (String word : words) {
                documents.add(dictionary.get(word).getDocumentsFound());
            }

            // merge all document lists using intersection logic
            ArrayList<Document> merged = intersection(documents.get(0), documents.get(1));
            for (int i = 2; i < words.length; i++) {
                merged = intersection(merged, documents.get(i));
            }

            // create the return string
            String result = query + " was found " + merged.size() + " times\n";
            for (Document document : merged) {
                result += document;
            }
            return result;

        } else if (words.length == 1) {
            return query + getWord(query);
        } else {
            return "Please enter valid query";
        }
    }

    private ArrayList<Document> intersection(ArrayList<Document> d1, ArrayList<Document> d2) {
        int index_1 = 0;
        int index_2 = 0;
        ArrayList<Document> merged = new ArrayList<>();
        while (index_1 < d1.size() && index_2 < d2.size()) {
            int docID_1 = d1.get(index_1).getDocumentNumber();
            int termFrequency_1 = d1.get(index_1).getDocumentFrequency();
            int docID_2 = d2.get(index_2).getDocumentNumber();
            int termFrequency_2 = d2.get(index_2).getDocumentFrequency();

            if (docID_1 == docID_2) {
                merged.add(new Document(docID_1, lowest(termFrequency_1, termFrequency_2)));
                index_1++;
                index_2++;
            } else if (docID_1 < docID_2) {
                index_1++;
            } else {
                index_2++;
            }
        }

        return merged;
    }

    // Get search results that contain all words in query (minus stop-words). This uses AND logic.
//    public String intersectionQuery(String query) {
//        Dictionary temp = new Dictionary();
//
//        int terminator = -1;
//        String[] words = Shared.getSearchTokens(query);
//
//        for (String word : words) {
//            if (!dictionary.containsKey(word)) {
//                return "Word not found";
//            }
//        }
//
//        if (words.length == 0) {
//            return "Word not found";
//        } else if (words.length == 1) {
//            return getWord(words[0]);
//        } else {
//            int[] index = new int[words.length];
//            TermInfo[] info = new TermInfo[words.length];
//            for (int i = 0; i < words.length; i++) {
//                index[i] = 0;
//                info[i] = dictionary.get(words[i]);
//            }
//
//            while (true) {
//                int[] docId = new int[words.length];
//                for (int j = 0; j < words.length; j++) {
//                    docId[j] = info[j].getDocument(index[j]).getDocumentNumber();
//                    if (terminator >= 0 && docId[j] > terminator)
//                        return temp.getWord(query);
//                }
//                if (allSame(docId)) {
//                    temp.submitWord(query, docId[0]);
//                    for (int j = 0; j < words.length; j++) {
//                        if (index[j] < info[j].getDocumentsFound().size() - 1)
//                            index[j]++;
//                        else
//                            return temp.getWord(query);
//                    }
//                } else if (allDocumentsExhausted(index, info)) {
//                    return temp.getWord(query);
//                } else {
//                    int highest = getHighest(docId);
//                    ArrayList<Integer> incrementIndexes = getIncrementIndexes(docId, highest);
//
//                    for (int incrementIndex : incrementIndexes) {
//                        // if one of the lists reaches end of the list
//                        int highestReached = highestReached(index, info, docId);
//                        if (highestReached > 0) {
//                            terminator = highestReached;
//                        }
//                        index[incrementIndex]++;
//                    }
//                }
//            }
//        }
//    }

    // Used by my wildcard query. Returns words in dictionary that begin with argument.
    public String getWordsBeginningWith(String beginsWith) {
        beginsWith = beginsWith.toLowerCase();
        ArrayList<String> sortedDictionary = new ArrayList<>(dictionary.keySet());
        Collections.sort(sortedDictionary);

        String output = "";
        int count = 0;

        for (String term : sortedDictionary) {
            if (term.length() >= beginsWith.length()) {
                boolean foundMatch = true;
                for (int i = 0; i < beginsWith.length(); i++) {
                    if (!term.substring(i, i+1).equals(beginsWith.substring(i, i+1))) {
                        foundMatch = false;
                    }
                }
                if (foundMatch) {
                    output += term + dictionary.get(term) + "\n";
                    count++;
                }
            }
        }
        output += "Dictionary has " + count + " terms starting with " + beginsWith;

        return output;
    }

    // Get top 25 results using the BM25 equation for a weighted score
    public String weightedQuery(String query) {
        String[] words = Shared.getSearchTokens(query);
        ArrayList<DocumentScore> scores = bm25(words);
        int count = 1;
        String result = "";
        for (DocumentScore score : scores) {
            if (count > 100) {
                break;
            }
            result += String.format("%-6s%-50s%n", count++ + ":", score);
        }
        return result;
    }

    // Returns a list of documents sorted by their bm25 score
    private ArrayList<DocumentScore> bm25(String[] words) {
        // this will be the results returned the caller of the method
        ArrayList<DocumentScore> scores = new ArrayList<>();

        // constants used in equation
        double N = Shared.NUMBER_OF_DOCUMENTS;
        double Lave = Shared.AVERAGE_DOCUMENT_LENGTH;
        double b = 0.5;
        double k1 = 2;

        for (int i = 0; i < words.length; i++) {
            // if word is not contained in dictionary, we do not need to add alter the scores
            if (dictionary.containsKey(words[i])) {
                TermInfo termInfo = dictionary.get(words[i]);                 // has statistics of word in dictionary
                DocumentFetcher fetcher = new DocumentFetcher();              // used to read documents in collection
                ArrayList<Document> documents = termInfo.getDocumentsFound(); // list documents of given word

                // variables dependent only on the word
                double DFt = (double) termInfo.getDocumentFrequency();
//                double TFtq = (double) termInfo.getTermFrequency();   can be added to 'long' query calculation (11.33)

                // iterate over all the documents for each word
                for (Document doc : documents) {

                    // variables dependent on the documents of the given document
                    int docId = doc.getDocumentNumber();
                    double Ld = (double) fetcher.getDocumentSize(doc.getDocumentNumber());
                    double TFtd = (double) doc.getDocumentFrequency();

                    // calculate score
                    double newScore = calculateBM25(N, Lave, b, k1, DFt, Ld, TFtd);

                    if (i > 0) { // do not need to check for duplicates if we are looking at our first word
                        for (int j = 0; j < scores.size(); j++) {
                            if (docId == scores.get(j).getDocumentId()) {
                                // Found a duplicate
                                newScore += scores.get(j).getScore();  // update score
                                scores.remove(j); // remove old value
                            }
                        }
                    }

                    // add new score
                    scores.add(new DocumentScore(docId, newScore));
                }
            }
        }
        Collections.sort(scores);
        return scores;
    }

    // Calculates bm25
    private double calculateBM25(double N, double Lave, double b, double k1, double DFt, double Ld, double TFtd) {
        double numerator = Math.log(N / DFt) * (k1 + 1) * TFtd;
        double denominator = k1 * ((1 - b) + b * (Ld / Lave)) + TFtd;
        return numerator/denominator;
    }

    // Determines if all the values of the array contain the same value
//    private boolean allSame(int[] docId) {
//        boolean match = true;
//        int first = docId[0];
//
//        for (int i = 1; i < docId.length; i++) {
//            if (docId[i] != first)
//                match = false;
//        }
//
//        return match;
//    }

    // Returns highest value stored in an array
//    private int getHighest(int[] docId) {
//        int highest = docId[0];
//        for (int i = 1; i < docId.length; i++) {
//            if (docId[i] > highest) {
//                highest = docId[i];
//            }
//        }
//        return highest;
//    }

    // Used by the intersection algorithm to check which document list needs to be incremented
//    private ArrayList<Integer> getIncrementIndexes(int[] docId, int highest) {
//        ArrayList<Integer> values = new ArrayList<>();
//        for (int i = 0; i < docId.length; i++) {
//            if (docId[i] < highest) {
//                values.add(i);
//            }
//        }
//
//        return values;
//    }

    // Used by the intersection to determine if all document lists are exhausted
//    private boolean allDocumentsExhausted(int[] index, TermInfo[] info) {
//        for (int i = 0; i < index.length; i++) {
//            if (index[i]+1 < info[i].getDocumentsFound().size()) {
//                return false;
//            }
//        }
//        return true;
//    }

    //    private int highestReached(int[] index, TermInfo[] info, int[] docId) {
//        for (int i = 0; i < index.length; i++) {
//            if (index[i] >= info[i].getDocumentsFound().size() - 1) {
//                return docId[i];
//            }
//        }
//        return -1;
//    }
    public int lowest(int a, int b) {
        return (a < b) ? a : b;
    }

    // Getters
    public Set<String> getKeySet() {
        return dictionary.keySet();
    }

    public int getTotalWordCount() {
        return totalWordCount;
    }

    public TermInfo getTermInfo(String token) {
        return dictionary.get(token);
    }

}
