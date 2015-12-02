package Dictionary;

import java.io.Serializable;

public class Document implements Serializable {
    private int documentNumber;
    private int termFrequency;

    public Document(int documentNumber) {
        this.documentNumber = documentNumber;
        this.termFrequency = 1;
    }

    public Document(int documentNumber, int documentFrequency) {
        this.documentNumber = documentNumber;
        this.termFrequency = documentFrequency;
    }

    public String toString() {
        String documentNumber = "doc_" + this.documentNumber + ":";
        String amount = termFrequency + " times";
        return String.format("  %-12s%-10s%n", documentNumber, amount);
    }

    public void addInstance() {
        this.termFrequency++;
    }

    public int getDocumentNumber() {
        return documentNumber;
    }

    public int getTermFrequency() {
        return termFrequency;
    }
}
