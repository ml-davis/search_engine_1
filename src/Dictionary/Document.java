package Dictionary;

import java.io.Serializable;

public class Document implements Serializable {
    private int documentNumber; // number of times term found in document
    private int documentFrequency;

    public Document(int documentNumber) {
        this.documentNumber = documentNumber;
        this.documentFrequency = 1;
    }

    public String toString() {
        return "\tdoc_" + documentNumber + ": " + documentFrequency + " times\n";
    }

    public void addInstance() {
        this.documentFrequency++;
    }

    public int getDocumentNumber() {
        return documentNumber;
    }

    public int getDocumentFrequency() {
        return documentFrequency;
    }
}
