package XmlParser;

public class TokenCounter {
    public static void main(String[] args) {
        DocumentFetcher fetcher = new DocumentFetcher();
        int count = 0;
        for (int i = 1; i <= Shared.NUMBER_OF_DOCUMENTS; i++) {
            String document = fetcher.readTitle(i);
            document += fetcher.readBody(i);
            String[] words = document.split(" ");
            count += words.length;
        }
        System.out.println(count);
    }
}
