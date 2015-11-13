package GUI;
import Dictionary.Dictionary;
import XmlParser.DocumentFetcher;
import XmlParser.PorterStemmer;
import XmlParser.Shared;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.Serializable;

public class Gui extends Application implements Serializable {

    @Override
    public void start(Stage primaryStage) {
        Dictionary dictionary = Shared.getDictionary();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Scene scene = new Scene(grid, 1920, 780);
        primaryStage.setTitle("Reuters Search Engine");
        primaryStage.setScene(scene);

        Text sceneTitle;
        if (Shared.STEMMED) {
            sceneTitle = new Text("Reuters Compressed Search Engine");
        } else {
            sceneTitle = new Text("Reuters Search Engine");
        }
        sceneTitle.setFont(Font.font("Arial", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 3, 1);

        // search results
        TextArea searchResults = new TextArea();
        searchResults.setPrefRowCount(35);
        searchResults.setEditable(false);
        ScrollPane searchScrollPane = new ScrollPane();
        searchScrollPane.setContent(searchResults);
        grid.add(searchResults, 0, 4, 3, 1);

        // document viewer
        TextArea documentOutput = new TextArea();
        documentOutput.setEditable(false);
        ScrollPane documentScrollPane = new ScrollPane();
        documentScrollPane.setContent(documentOutput);
        grid.add(documentOutput, 3, 4, 3, 1);

        // intersection search
        Label intersectionSearchLabel = new Label("Intersection Search:");
        grid.add(intersectionSearchLabel, 0, 1);
        TextField intersectionSearchField = new TextField();
        intersectionSearchField.setPrefWidth(350);
        grid.add(intersectionSearchField, 1, 1);
        Button intersectionSearchButton = new Button("Search");
        intersectionSearchButton.setOnAction(e -> {
            String searchItem = Shared.filterString(intersectionSearchField.getText());
            if (searchItem.equals("")) {
                searchResults.setText("Please enter search term");
            } else {
                if (Shared.STEMMED) {
                    searchItem = stemSearchTerm(searchItem);
                }
                String searchResult = dictionary.evaluateQuery(searchItem);
                searchResults.setText(searchResult);
            }
        });
        grid.add(intersectionSearchButton, 2, 1);

        // wildcard search
        Label wildcardLabel = new Label("Words Starting With:");
        grid.add(wildcardLabel, 0, 2);
        TextField wildcardField = new TextField();
        grid.add(wildcardField, 1, 2);
        Button wildcardButton = new Button("Search");
        wildcardButton.setOnAction(e -> {
            String term = wildcardField.getText();
            String output;
            if (term.equals("")) {
                output = "Please enter the beginning of the word that you are interested in";
            } else {
                output = dictionary.getWordsBeginningWith(term);
            }
            searchResults.setText(output);
        });
        grid.add(wildcardButton, 2, 2);

        // weighted search
        Label weightedSearchLabel = new Label("Weighted Search:");
        grid.add(weightedSearchLabel, 0, 3);
        TextField weightedSearchField = new TextField();
        grid.add(weightedSearchField, 1, 3);
        Button weightedSearchButton = new Button("Search");
        weightedSearchButton.setOnAction(e -> {
            String term = weightedSearchField.getText();
            String output = "Weighted search results for: " + term + "\n\n";
            output += dictionary.weightedQuery(Shared.filterString(term));
            searchResults.setText(output);
        });
        grid.add(weightedSearchButton, 2, 3);

        // view document
        Label viewDocumentLabel = new Label("View Document:");
        grid.add(viewDocumentLabel, 3, 3);
        TextField viewDocumentField = new TextField();
        viewDocumentField.setPrefWidth(350);
        grid.add(viewDocumentField, 4, 3);
        Button viewDocumentButton = new Button("View");
        viewDocumentButton.setOnAction(e -> {
            try {
                int input = Integer.parseInt(viewDocumentField.getText());
                DocumentFetcher fetcher = new DocumentFetcher();
                documentOutput.setText(fetcher.readTitle(input) + "\n");
                documentOutput.appendText(fetcher.readBody(input));
            } catch (Exception e1) {
                documentOutput.setText("Please enter the document number as an integer value");
            }
        });
        grid.add(viewDocumentButton, 5, 3);


        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> {
            searchResults.setText("");
            documentOutput.setText("");
            intersectionSearchField.setText("");
            wildcardField.setText("");
            weightedSearchField.setText("");
            viewDocumentField.setText("");
        });
        grid.add(clearButton, 0, 5);
        String wordCountString = "";
        // add spacing for right alignment
        for (int i = 0; i < 39; i++) {
            wordCountString += " ";
        }
        wordCountString += "Number of terms in Dictionary: " + dictionary.getTotalWordCount();
        Label wordCount = new Label(wordCountString);
        grid.add(wordCount, 4, 5, 2, 1);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public static String stemSearchTerm(String query){
        PorterStemmer ps = new PorterStemmer();
        String[] searchTerms = query.split(" ");
        for (int i = 0; i < searchTerms.length; i++) {
            String s1 = ps.step1(searchTerms[i]);
            String s2 = ps.step2(s1);
            String s3= ps.step3(s2);
            String s4= ps.step4(s3);
            String s5= ps.step5(s4);
            searchTerms[i] = s5;
        }
        String normalizedQuery = "";
        for (int i = 0; i < searchTerms.length; i++) {
            normalizedQuery += searchTerms[i] + " ";
        }
        return normalizedQuery;
    }

}
