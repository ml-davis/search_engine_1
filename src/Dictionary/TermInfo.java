package Dictionary;

import java.io.Serializable;
import java.util.ArrayList;

public class TermInfo implements Serializable {
    private int collectionFrequency;
    private ArrayList<Document> documentsFound;

    // Constructor
    public TermInfo(int documentNumber) {
        this.collectionFrequency = 1;
        documentsFound = new ArrayList<>();
        documentsFound.add(new Document(documentNumber));
    }

    // Add document documentsFound. If document already exists in list. Increase termFrequency in Document object
    public void addTermOccurrence(int documentNumber) {
        collectionFrequency++;
        for (Document document : documentsFound) {
            // if document already in list
            if (document.getDocumentNumber() == documentNumber) {
                document.addInstance(); // increase term frequency
                return;
            }
        }
        // if document not already in list
        documentsFound.add(new Document(documentNumber));
    }

    public String toString() {
        if (documentsFound.size() > 0) {
            String toString = " was found " + collectionFrequency + " times in " + documentsFound.size() + " documents\n";
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

    public int getDocumentFrequency() {
        return documentsFound.size();
    }

    public ArrayList<Document> getDocumentsFound() {
        return documentsFound;
    }
}
