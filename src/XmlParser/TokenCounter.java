package XmlParser;

// Quick and simple class made to compute a constant needed in bm25 algorithm
public class TokenCounter {
    public static void main(String[] args) {
        DocumentFetcher fetcher = new DocumentFetcher();
        int count = 0;
        for (int i = 1; i <= Shared.NUMBER_OF_DOCUMENTS; i++) {
            String document = fetcher.getTitle(i);
            document += fetcher.getBody(i);
            String[] words = Shared.getSearchTokens(document);
            count += words.length;
        }
        System.out.println(count);
    }
}
