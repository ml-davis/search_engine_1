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
        grid.add(searchResults, 0, 3, 3, 1);

        // document viewer
        TextArea documentOutput = new TextArea();
        documentOutput.setEditable(false);
        ScrollPane documentScrollPane = new ScrollPane();
        documentScrollPane.setContent(documentOutput);
        grid.add(documentOutput, 3, 3, 3, 1);

        Label searchLabel = new Label("Search:");
        grid.add(searchLabel, 0, 1);
        TextField searchField = new TextField();
        searchField.setPrefWidth(420);
        grid.add(searchField, 1, 1);
        Button searchButton = new Button("Search");
        searchButton.setOnAction(e -> {
            String searchItem = searchField.getText();
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
        grid.add(searchButton, 2, 1);

        Label browseLabel = new Label("Browse:");
        grid.add(browseLabel, 0, 2);
        TextField browseField = new TextField();
        grid.add(browseField, 1, 2);
        Button browseButton = new Button("Browse");
        browseButton.setOnAction(e -> {
            String term = browseField.getText();
            String output;
            if (term.equals("")) {
                output = "Please enter the beginning of the word that you are interested in";
            } else {
                output = dictionary.getWordsBeginningWith(term);
            }
            searchResults.setText(output);
        });
        grid.add(browseButton, 2, 2);

        Label viewDocumentLabel = new Label("  View Document:");
        grid.add(viewDocumentLabel, 3, 2);
        TextField viewDocumentField = new TextField();
        viewDocumentField.setPrefWidth(350);
        grid.add(viewDocumentField, 4, 2);
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
        grid.add(viewDocumentButton, 5, 2);


        Button clearButton = new Button("Clear");
        clearButton.setOnAction(e -> {
            searchResults.setText("");
            documentOutput.setText("");
            searchField.setText("");
            browseField.setText("");
            viewDocumentField.setText("");
        });
        grid.add(clearButton, 0, 4);
        String wordCountString = "";
        // add spacing for right alignment
        for (int i = 0; i < 39; i++) {
            wordCountString += " ";
        }
        wordCountString += "Number of terms in Dictionary: " + dictionary.getTotalWordCount();
        Label wordCount = new Label(wordCountString);
        grid.add(wordCount, 4, 4, 2, 1);

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
