package Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import Enum.FileFormat;

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
    
    /**
     * Detects the FileFormat based on the file extension.
     * @param fileName The name or path of the file.
     * @return The corresponding FileFormat enum.
     * @throws IllegalArgumentException if the extension is missing or unsupported.
     */
    public static FileFormat detectFormat(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("File name is invalid or missing extension: " + fileName);
        }
        
        String extension = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
        
        return switch (extension) {
            case ".csv" -> FileFormat.CSV;
            case ".tsv", ".txt" -> FileFormat.TSV;
            default -> throw new IllegalArgumentException("Unsupported file format: " + extension + ". Supported formats are .csv, .tsv, and .txt.");
        };
    }
}
