package XmlParser;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocumentFetcher implements Serializable {

    public DocumentFetcher() {
        // default constructor
    }

    public ArrayList<String> getTokens(int documentNumber) {
        String document = readTitle(documentNumber) + " " + readBody(documentNumber);
        String[] words = Shared.getSearchTokens(document);
        ArrayList<String> tokens = new ArrayList<>();
        for (String token : words) {
            if (!token.equals("")) {
                tokens.add(token);
            }
        }
        return tokens;
    }



    public int getDocumentSize(int documentNumber) {
        String document = readTitle(documentNumber) + " " + readBody(documentNumber);
        String[] tokenArray = Shared.getSearchTokens(document);
        return tokenArray.length;
    }

    public String readTitle(int documentNumber) {
        Pattern pattern = Pattern.compile("<TITLE>(.+?)</TITLE>");
        Matcher matcher = pattern.matcher(getDocumentString(documentNumber));

        if (matcher.find()) {
            return matcher.group(1) + "";
        } else {
            // title not found
            return "";
        }
    }

    public String readBody(int documentNumber) {
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

    public String getDocumentString(int documentNumber) {
        Scanner reader = xmlReader(documentNumber);
        String doc = "";
        while (reader.hasNextLine()) {
            doc += reader.nextLine() + "\n";
        }
        reader.close();

        return doc;
    }

    public String getDocumentLine(int documentNumber, int lineNumber) {
        Scanner reader = xmlReader(documentNumber);
        for (int i = 0; i < lineNumber - 1; i++) {
            reader.nextLine();
        }
        String line = reader.nextLine();
        reader.close();

        return line;
    }

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
