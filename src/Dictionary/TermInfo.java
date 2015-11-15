package Dictionary;

import java.io.Serializable;
import java.util.ArrayList;

public class TermInfo implements Serializable {
    private int termFrequency;  // number of times word found in collection
    private ArrayList<Document> documentsFound; // list of all terms documents

    // Constructor
    public TermInfo(int documentNumber) {
        this.termFrequency = 1;
        documentsFound = new ArrayList<>();
        documentsFound.add(new Document(documentNumber));
    }

    // Add document documentsFound. If document already exists in list. Increase documentFrequency in Document object
    public void addTermOccurrence(int documentNumber) {
        for (Document document : documentsFound) {
            // if document already in list
            if (document.getDocumentNumber() == documentNumber) {
                termFrequency++;
                document.addInstance();
                return;
            }
        }
        // if document not already in list
        termFrequency++;
        documentsFound.add(new Document(documentNumber));
    }

    public String toString() {
        if (documentsFound.size() > 0) {
            String toString = " was found " + termFrequency + " times in " + documentsFound.size() + " documents\n\n";
            for (Document document : documentsFound) {
                toString += document;
            }
            return toString ;
        } else {
            return " was not found";
        }
    }

    // Get specific document in list
    public Document getDocument(int i) {
        return documentsFound.get(i);
    }

    // Generic getters
    public int getDocumentFrequency() {
        return documentsFound.size();
    }

    public int getTermFrequency() {
        return this.termFrequency;
    }

    public ArrayList<Document> getDocumentsFound() {
        return documentsFound;
    }


}
