package Dictionary;

import XmlParser.DocumentFetcher;
import XmlParser.Shared;

import java.io.Serializable;
import java.util.*;

public class Dictionary implements Serializable {
    private int totalWordCount;

    private HashMap<String, TermInfo> dictionary;

    public Dictionary() {
        this.totalWordCount = 0;
        this.dictionary = new HashMap<>();
    }

    public void submitWord(String word, int documentNumber) {
        if (!dictionary.containsKey(word)) {
            totalWordCount++;
            dictionary.put(word, new TermInfo(documentNumber));
        } else {
            dictionary.get(word).addTermOccurrence(documentNumber);
        }
    }

    public String intersectionQuery(String query) {

        Dictionary temp = new Dictionary();

        int terminator = -1;
        String[] words = Shared.getSearchTokens(query);
        int[] index = new int[words.length];
        TermInfo[] info = new TermInfo[words.length];
        for (int i = 0; i < words.length; i++) {
            System.out.print(words[i] + " ");
            index[i] = 0;
            info[i] = dictionary.get(words[i]);
            System.out.println(info[i].getDocumentsFound().size());
        }
        System.out.println();

        while (true) {
            int[] docId = new int[words.length];
            for (int j = 0; j < words.length; j++) {
                System.out.print(info[j].getDocument(index[j]).getDocumentNumber() + " ");
                docId[j] = info[j].getDocument(index[j]).getDocumentNumber();
                if (terminator >= 0 && docId[j] > terminator)
                    return temp.getWord(query);
            }
            System.out.println();
            if (allSame(docId)) {
                temp.submitWord(query, docId[0]);
//                solutions.add(docId[0]);
                System.out.println("Added " + docId[0]);
                for (int j = 0; j < words.length; j++) {
                    if (index[j] < info[j].getDocumentsFound().size() - 1)
                        index[j]++;
                    else
                        return temp.getWord(query);
                }
            } else {
                int highest = getHighestDocId(docId);
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

            System.out.println();
        }

    }

    // evaluate query using AND logic between the words
    public String intersectionQueryOld(String query) {
        String[] words = Shared.getSearchTokens(query);
        if (words.length == 1) {
            return getWord(query);
        } else if (words.length == 2) {
            ArrayList<Document> term_0_docs = dictionary.get(words[0]).getDocumentsFound();
            ArrayList<Document> term_1_docs = dictionary.get(words[1]).getDocumentsFound();
            int index_0 = 0;
            int index_1 = 0;
            Dictionary temp = new Dictionary();
            while (index_0 < term_0_docs.size() && index_1 < term_1_docs.size()) {
                int docID_0 = term_0_docs.get(index_0).getDocumentNumber();
                int docID_1 = term_1_docs.get(index_1).getDocumentNumber();
                if (docID_0 == docID_1) {
                    temp.submitWord(query, docID_0);
                    index_0++;
                    index_1++;
                } else if (docID_0 < docID_1) {
                    index_0++;
                } else {
                    index_1++;
                }
            }
            if (temp.dictionary.containsKey(query)) {
                return temp.getWord(query);
            } else {
                return "Word not found";
            }
        }
        return "Please limit your search to 2 words :/";
    }

    // get top 25 results of weighted score
    public String weightedQuery(String query) {
        String[] words = Shared.getSearchTokens(query);
        TreeSet<DocumentScore> scores = bm25(words);
        Iterator<DocumentScore> iterator = scores.iterator();
        int count = 0;
        String result = "";
        while (iterator.hasNext() && count < 25) {
            result += iterator.next() + "\n";
            count++;
        }

        return result;
    }

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

    private double calculateBM25(double N, double Lave, double b, double k1, double DFt, double Ld, double TFtd) {
        double numerator = Math.log(N / DFt) * (k1 + 1) * TFtd;
        double denominator = k1 * ((1 - b) + b * (Ld / Lave)) + TFtd;
        return numerator/denominator;
    }

    public String getWord(String word) {
        if (dictionary.containsKey(word)) {
            return word + dictionary.get(word);
        } else {
            return "Word not found";
        }
    }

    private boolean allSame(int[] docId) {
        boolean match = true;
        int first = docId[0];

        for (int i = 1; i < docId.length; i++) {
            if (docId[i] != first)
                match = false;
        }

        return match;
    }

    private int getHighestDocId(int[] docId) {
        int highest = docId[0];
        for (int i = 1; i < docId.length; i++) {
            if (docId[i] > highest) {
                highest = docId[i];
            }
        }
        return highest;
    }

    private ArrayList<Integer> getIncrementIndexes(int[] docId, int highest) {
        ArrayList<Integer> values = new ArrayList<>();
        for (int i = 0; i < docId.length; i++) {
            if (docId[i] < highest) {
                values.add(i);
            }
        }

        return values;
    }

    private int getHighestIndex(int[] docId, int highest) {
        int index = -1;
        for (int i = 0; i < docId.length; i++) {
            if (docId[i] == highest) {
                index = i;
            }
        }
        return index;
    }

    public String getWordsBeginningWith(String beginsWith) {
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

    public void showDictionary(int start, int end) {
        ArrayList<String> sortedDictionary = new ArrayList<>(dictionary.keySet());
        System.out.println("Sorting dictionary...");
        Collections.sort(sortedDictionary);
        System.out.println("Printing term " + start + " to " + end);
        for (int i = start; i < end; i++) {
            System.out.println(sortedDictionary.get(i));
        }
    }

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
