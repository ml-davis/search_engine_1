package Dictionary;

public class DocumentScore implements Comparable<DocumentScore> {
    private int documentId;
    private double score;

    public DocumentScore(int documentId, double score) {
        this.documentId = documentId;
        this.score = score;
    }

    public String toString() {
        if (documentId >= 1000) {
            return "Document " + documentId + "\t\tScore: " + score;
        } else {
            return "Document " + documentId + "\t\t\tScore: " + score;
        }
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
