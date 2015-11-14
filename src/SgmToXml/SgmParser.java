package SgmToXml;

import XmlParser.Shared;

import java.io.*;
import java.util.Arrays;
import java.util.Scanner;

// Parses all of the .sgm files into XML files and store them into Shared.XML_FILE_PATH
public class SgmParser {
    public void parseFiles() {
        File sgmFolder = new File(Shared.SGM_FILE_PATH);
        File[] sgmFiles = sgmFolder.listFiles();

        if (sgmFiles == null) {
            System.out.println("Problem reading from directory: " + sgmFolder);
        } else {
            int xmlCount = 1;
            Arrays.sort(sgmFiles);

            for (File sgmFile : sgmFiles) {

                String sgmFileName = Shared.SGM_FILE_PATH + sgmFile.getName();
                Scanner reader = reader(new File(sgmFileName));
                System.out.println("Parsing " + sgmFileName);

                String xmlFileName = Shared.XML_FILE_PATH + "doc_" + xmlCount++ + ".xml";
                PrintWriter outputStream = writer(xmlFileName);

                while (reader.hasNextLine()) {
                    String line = reader.nextLine();
                    outputStream.println(line);
                    if (line.equals("</REUTERS>") && reader.hasNextLine()) {
                        outputStream.close();
                        xmlFileName = Shared.XML_FILE_PATH + "doc_" + xmlCount++ + ".xml";
                        outputStream = writer(xmlFileName);
                    }
                }
                outputStream.close();
            }
        }
    }

    private Scanner reader(File file) {
        Scanner inputStream = null;
        try {
            inputStream = new Scanner(new FileInputStream(file));
        } catch (FileNotFoundException e) {
            System.out.println("File was not found: " + file.getPath());
            System.exit(0);
        }
        return inputStream;
    }

    public static PrintWriter writer(String destination) {
        PrintWriter outputStream = null;
        try {
            outputStream = new PrintWriter(new FileOutputStream(destination));
        } catch (FileNotFoundException e) {
            System.out.println("Error opening write destination: " + destination);
            System.exit(0);
        }
        return outputStream;
    }

}
