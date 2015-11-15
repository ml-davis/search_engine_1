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

    // Get search results that contain all words in query (minus stop-words). This uses AND logic.
    public String intersectionQuery(String query) {
        Dictionary temp = new Dictionary();

        int terminator = -1;
        String[] words = Shared.getSearchTokens(query);

        if (words.length == 0) {
            return "Word not found";
        } else if (words.length == 1) {
            return getWord(words[0]);
        } else {
            int[] index = new int[words.length];
            TermInfo[] info = new TermInfo[words.length];
            for (int i = 0; i < words.length; i++) {
                index[i] = 0;
                info[i] = dictionary.get(words[i]);
            }

            while (true) {
                int[] docId = new int[words.length];
                for (int j = 0; j < words.length; j++) {
                    docId[j] = info[j].getDocument(index[j]).getDocumentNumber();
                    if (terminator >= 0 && docId[j] > terminator)
                        return temp.getWord(query);
                }
                if (allSame(docId)) {
                    temp.submitWord(query, docId[0]);
                    for (int j = 0; j < words.length; j++) {
                        if (index[j] < info[j].getDocumentsFound().size() - 1)
                            index[j]++;
                        else
                            return temp.getWord(query);
                    }
                } else if (allDocumentsExhausted(index, info)) {
                    return temp.getWord(query);
                } else {
                    int highest = getHighest(docId);
                    int highestIndex = getHighestIndex(docId, highest);
                    ArrayList<Integer> incrementIndexes = getIncrementIndexes(docId, highest);

                    for (int incrementIndex : incrementIndexes) {
                        // if one of the lists reaches end of the list
                        if (index[highestIndex] >= info[highestIndex].getDocumentsFound().size() - 1) {
                            terminator = highest;
                        }
                        index[incrementIndex]++;
                    }
                }
            }
        }
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
        TreeSet<DocumentScore> scores = bm25(words);
        Iterator<DocumentScore> iterator = scores.iterator();
        int count = 1;
        String result = "";
        while (iterator.hasNext() && count <= 100) {
            result += count++ + ":\t" + iterator.next() + "\n";
        }

        return result;
    }

    // Returns a set of documents sorted by their bm25 score
    private TreeSet<DocumentScore> bm25(String[] words) {
        TreeSet<DocumentScore> scores = new TreeSet<>();

        // constants
        double N = Shared.NUMBER_OF_DOCUMENTS;
        double Lave = Shared.AVERAGE_DOCUMENT_LENGTH;
        double b = 0.5;
        double k1 = 2;

        for (String word: words) {
            if (dictionary.containsKey(word)) {
                TermInfo termInfo = dictionary.get(word);
                DocumentFetcher fetcher = new DocumentFetcher();
                ArrayList<Document> document = termInfo.getDocumentsFound();

                // variables dependent only on the word
                double DFt = (double) termInfo.getDocumentFrequency();
//                double TFtq = (double) termInfo.getTermFrequency();   can be added to 'long' query calculation

                for (Document doc : document) {

                    // variables dependent on the documents of the given word
                    int documentNumber = doc.getDocumentNumber();
                    double Ld = (double) fetcher.getDocumentSize(doc.getDocumentNumber());
                    double TFtd = (double) doc.getDocumentFrequency();

                    // calculate score
                    double currentScore = calculateBM25(N, Lave, b, k1, DFt, Ld, TFtd);

                    // add scores to set
                    Iterator<DocumentScore> iterator = scores.iterator();
                    while (iterator.hasNext()) { // check if document is already in set
                        DocumentScore score = iterator.next();
                        // if document already in set, add oldScore to currentScore, remove old entry
                        if (score.getDocumentId() == documentNumber) {
                            currentScore += score.getScore();
                            iterator.remove();
                        }
                    }
                    // add score to set
                    scores.add(new DocumentScore(documentNumber, currentScore));
                }
            }
        }
        return scores;
    }

    // Calculates bm25
    private double calculateBM25(double N, double Lave, double b, double k1, double DFt, double Ld, double TFtd) {
        double numerator = Math.log(N / DFt) * (k1 + 1) * TFtd;
        double denominator = k1 * ((1 - b) + b * (Ld / Lave)) + TFtd;
        return numerator/denominator;
    }

    // Determines if all the values of the array contain the same value
    private boolean allSame(int[] docId) {
        boolean match = true;
        int first = docId[0];

        for (int i = 1; i < docId.length; i++) {
            if (docId[i] != first)
                match = false;
        }

        return match;
    }

    // Returns highest value stored in an array
    private int getHighest(int[] docId) {
        int highest = docId[0];
        for (int i = 1; i < docId.length; i++) {
            if (docId[i] > highest) {
                highest = docId[i];
            }
        }
        return highest;
    }

    // Used by the intersection algorithm to check which document list needs to be incremented
    private ArrayList<Integer> getIncrementIndexes(int[] docId, int highest) {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < docId.length; i++) {
            if (docId[i] < highest) {
                values.add(i);
            }
        }

        return values;
    }

    // Used by the intersection algorithm to determine which document list doesn't need to be incremented
    private int getHighestIndex(int[] docId, int highest) {
        int index = -1;
        for (int i = 0; i < docId.length; i++) {
            if (docId[i] == highest) {
                index = i;
            }
        }
        return index;
    }

    // Used by the intersection to determine if all document lists are exhausted
    private boolean allDocumentsExhausted(int[] index, TermInfo[] info) {
        for (int i = 0; i < index.length; i++) {
            if (index[i]+1 < info[i].getDocumentsFound().size()) {
                return false;
            }
        }
        return true;
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
