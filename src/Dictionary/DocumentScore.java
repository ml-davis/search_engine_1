package Dictionary;

public class DocumentScore implements Comparable<DocumentScore> {
    private int documentId;
    private double score;

    public DocumentScore(int documentId, double score) {
        this.documentId = documentId;
        this.score = score;
    }

    public String toString() {
        return String.format("%-20s%-7s%7.3f", "doc_" + documentId, "Score:", score);
    }

    @Override
    public int compareTo(final DocumentScore o) {
        return -Double.compare(this.score, o.score);
    }

    public int getDocumentId() {
        return documentId;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double amount) {
        this.score = amount;
    }
}
