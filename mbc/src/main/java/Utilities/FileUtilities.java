package Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileUtilities {

    public static String[] getFileLines (String fileName) {
        try {
            return Files.readAllLines(Paths.get(fileName)).toArray(new String[0]);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
        }
    }

    public static String[] getSplitDataRow(String dataRow, String splitChar) {
        return dataRow.split(splitChar);
    }
}
