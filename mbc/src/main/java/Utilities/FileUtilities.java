package Utilities;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import Enum.FileFormat;

/**
 * Utility class for common file input/output operations and path manipulations.
 */
public class FileUtilities {

    /**
     * Reads all lines from a file and returns them as an array of strings.
     * 
     * @param fileName the path to the file
     * @return an array of strings containing the file's lines
     * @throws RuntimeException if an I/O error occurs
     */
    public static String[] getFileLines(String fileName) {
        try {
            return Files.readAllLines(Paths.get(fileName)).toArray(new String[0]);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + fileName, e);
        }
    }

    /**
     * Splits a single data row string by a specified delimiter.
     * 
     * @param dataRow the row string to split
     * @param splitChar the delimiter string (e.g., "," or "\t")
     * @return an array of split string components
     */
    public static String[] getSplitDataRow(String dataRow, String splitChar) {
        return dataRow.split(splitChar);
    }
    
    /**
     * Detects the FileFormat enum based on the file extension.
     * 
     * @param fileName the name or path of the file
     * @return the corresponding FileFormat enum
     * @throws IllegalArgumentException if the extension is missing or unsupported
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

    /**
     * Appends a suffix to the file name directly before the extension.
     * Example: "output.txt" + "_benchmarks" becomes "output_benchmarks.txt"
     * 
     * @param fileName the original file name
     * @param suffix the suffix string to append
     * @return the newly formatted file name string
     */
    public static String appendSuffixToFileName(String fileName, String suffix) {
        if (fileName == null || !fileName.contains(".")) {
            return fileName + suffix;
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        String baseName = fileName.substring(0, lastDotIndex);
        String extension = fileName.substring(lastDotIndex);
        return baseName + suffix + extension;
    }
}
