package XmlParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// This class is used to gain easy-access to documents and info about those documents
public class DocumentFetcher implements Serializable {

    public DocumentFetcher() {
        // default constructor
    }

    // Returns list of all the words in a specific document.
    public ArrayList<String> getTokens(int documentNumber) {
        String document = getTitle(documentNumber) + " " + getBody(documentNumber);
        String[] words = Shared.getSearchTokens(document);
        ArrayList<String> tokens = new ArrayList<>();
        for (String token : words) {
            if (!token.equals("")) {
                tokens.add(token);
            }
        }
        return tokens;
    }

    // Returns the size of a specific document
    public int getDocumentSize(int documentNumber) {
        String document = getTitle(documentNumber) + " " + getBody(documentNumber);
        String[] tokenArray = Shared.getSearchTokens(document);
        return tokenArray.length;
    }

    // Returns the title of a specific document
    public String getTitle(int documentNumber) {
        Pattern pattern = Pattern.compile("<TITLE>(.+?)</TITLE>");
        Matcher matcher = pattern.matcher(getDocumentString(documentNumber));

        if (matcher.find()) {
            return matcher.group(1) + "";
        } else {
            // title not found
            return "";
        }
    }

    // Returns the body of a specific document
    public String getBody(int documentNumber) {
        // (?s) treats whole body as one line
        Pattern pattern = Pattern.compile("(?s)<BODY>(.+?)</BODY>");
        Matcher matcher = pattern.matcher(getDocumentString(documentNumber));

        if (matcher.find()) {
            String body = matcher.group(1);
            // (?i) means case insensitive
            body = body.replaceAll("(?i)REUTER\\n&#3;", "");
            return body;
        } else {
            // title not found
            return "";
        }
    }

    // Gets entire document, including all tags
    public String getDocumentString(int documentNumber) {
        Scanner reader = xmlReader(documentNumber);
        String doc = "";
        while (reader.hasNextLine()) {
            doc += reader.nextLine() + "\n";
        }
        reader.close();

        return doc;
    }

    // Get a specific line of a specific document
    public String getDocumentLine(int documentNumber, int lineNumber) {
        Scanner reader = xmlReader(documentNumber);
        for (int i = 0; i < lineNumber - 1; i++) {
            reader.nextLine();
        }
        String line = reader.nextLine();
        reader.close();

        return line;
    }

    // The Scanner used to read the documents
    private Scanner xmlReader(int documentNumber) {
        Scanner inputStream = null;
        String path = Shared.XML_FILE_PATH + "doc_" + documentNumber + ".xml";
        try {
            File file = new File(path);
            inputStream = new Scanner(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.out.println("File was not found: " + path);
            System.exit(0);
        }
        return inputStream;
    }
}
