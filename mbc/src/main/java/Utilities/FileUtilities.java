package Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtilities {

    public static String[] getFileLines (String fileName) {
        String[] fileLines = null;
        try {
            fileLines = Files.readAllLines(Paths.get(fileName)).toArray(new String[0]);
        } catch (IOException e) {
            System.out.println("Error reading gene expression file: " + e.getMessage());
        }
        return fileLines;
    }

    public static String[] getSplitDataRow(String dataRow, String splitChar) {
        return dataRow.split(splitChar);
    }
}
