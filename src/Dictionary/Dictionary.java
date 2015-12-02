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
            dictionary.put(word, new TermInfo(documentNumber)); // add new word
        } else {
            dictionary.get(word).addTermOccurrence(documentNumber); // update word info
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

    // Returns results that have all words in query ( t1 AND t2 AND .... tn )
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
            return getWord(query);
        } else {
            return "Please enter valid query";
        }
    }

    // Returns the intersection of two lists  {1, 2, 3} ^ {3, 4, 5} = {3}
    private ArrayList<Document> intersection(ArrayList<Document> d1, ArrayList<Document> d2) {
        int index_1 = 0;
        int index_2 = 0;
        ArrayList<Document> merged = new ArrayList<>();
        while (index_1 < d1.size() && index_2 < d2.size()) {
            int docID_1 = d1.get(index_1).getDocumentNumber();
            int termFrequency_1 = d1.get(index_1).getTermFrequency();
            int docID_2 = d2.get(index_2).getDocumentNumber();
            int termFrequency_2 = d2.get(index_2).getTermFrequency();

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
            if (count > 20) {
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
                    double TFtd = (double) doc.getTermFrequency();

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

    // Returns the lowest integer argument
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
