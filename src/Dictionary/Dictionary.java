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

    public String evaluateQuery2(String query) {
        Dictionary solution = new Dictionary();
        String[] words = query.split(" ");
        TermInfo[] info = new TermInfo[words.length];
        for (int i = 0; i < words.length; i++) {
            info[i] = dictionary.get(words[i]);
        }

        return "";
    }

    public void bm25(String[] words) {

        TreeSet<DocumentScore> scores = new TreeSet<>();

        for (String word: words) {

            TermInfo termInfo = dictionary.get(word);
            DocumentFetcher fetcher = new DocumentFetcher();
            ArrayList<Document> document = termInfo.getDocumentsFound();

            System.out.printf("%-6s:%10s%n", "Word", word);
            double N = Shared.NUMBER_OF_DOCUMENTS;
            System.out.printf("%-6s:%10.3f%n", "N", N);
            double DFt = (double) termInfo.getDocumentFrequency();
            System.out.printf("%-6s:%10.3f%n", "DFt", DFt);
            double k1 = 1.2;
            System.out.printf("%-6s:%10.3f%n", "k1", k1);
            double b = 0.75;
            System.out.printf("%-6s:%10.3f%n", "b", b);
            double Lave = Shared.AVERAGE_DOCUMENT_LENGTH;
            System.out.printf("%-6s:%10.3f%n", "Lave", Lave);
            double TFtq = (double) termInfo.getTermFrequency();
            System.out.printf("%-6s:%10.3f%n", "TFtq", TFtq);
            for (Document doc : document) {
                int documentNumber = doc.getDocumentNumber();
                System.out.printf("%-6s:%10d%n", "Doc", documentNumber);
                double Ld = (double) fetcher.getDocumentSize(doc.getDocumentNumber());
                System.out.printf("%-6s:%10.3f%n", "Ld", Ld);
                double TFtd = (double) doc.getDocumentFrequency();
                System.out.printf("%-6s:%10.3f%n", "TFtd", TFtd);
                double numerator = Math.log(N/DFt)*(k1+1)*TFtd;
                double denominator = k1*((1-b)+b*(Ld/Lave))+ TFtd;
                double currentScore = numerator/denominator;
                System.out.println("This term has a score of: " + currentScore + "\n");
                System.out.println();


                // check if document already in set, if so increment currentScore by old value
                Iterator<DocumentScore> iterator = scores.iterator();

                while (iterator.hasNext()) {
                    DocumentScore score = iterator.next();
                    if (score.getDocumentId() == documentNumber) {
                        currentScore += score.getScore();
                        iterator.remove();
                    }
                }
                scores.add(new DocumentScore(documentNumber, currentScore));
            }
            for (DocumentScore s : scores) {
                System.out.println(s);
            }

            System.out.println();
        }
    }



    // evaluate query using AND logic between the wo
    public String evaluateQuery(String query) {
        String[] words = query.split(" ");
        if (words.length == 1) {
            return printWord(query);
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
                return temp.printWord(query);
            } else {
                return "Word not found";
            }
        }
        return "Please limit your search to 2 words :/";
    }

    public String printWord(String word) {
        if (dictionary.containsKey(word)) {
            return word + dictionary.get(word);
        } else {
            return "Word not found";
        }
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

    public Set<String> getKeySet() {
        return dictionary.keySet();
    }

    public String getWords(int start, int end) {
        ArrayList<String> sortedDictionary = new ArrayList<>(dictionary.keySet());
        System.out.println("Sorting dictionary...");
        Collections.sort(sortedDictionary);
        System.out.println("Printing term " + start + " to " + end);
        String output = "";
        for (int i = start; i < end; i++) {
            output += sortedDictionary.get(i) + "\n";
        }
        output += "Dictionary contains " + totalWordCount + " terms.";
        return output;
    }

    public void printDictionary() {
        ArrayList<String> sortedDictionary = new ArrayList<>(dictionary.keySet());
        Collections.sort(sortedDictionary);
        for (String term : sortedDictionary) {
            System.out.println(term + dictionary.get(term));
        }
        System.out.println("Dictionary contains " + totalWordCount + " terms.");
    }

    public int getTotalWordCount() {
        return totalWordCount;
    }

    public TermInfo getTermInfo(String token) {
        return dictionary.get(token);
    }

}
