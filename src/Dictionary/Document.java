package Dictionary;

import java.io.Serializable;

public class Document implements Serializable {
    private int documentNumber; // number of times term found in document
    private int documentFrequency;

    public Document(int documentNumber) {
        this.documentNumber = documentNumber;
        this.documentFrequency = 1;
    }

    public Document(int documentNumber, int documentFrequency) {
        this.documentNumber = documentNumber;
        this.documentFrequency = documentFrequency;
    }

    public String toString() {
        String documentNumber = "doc_" + this.documentNumber + ":";
        String amount = documentFrequency + " times";
        return String.format("  %-12s%-10s%n", documentNumber, amount);
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
