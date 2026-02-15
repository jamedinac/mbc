package Enum;

public enum FileFormat {
    TSV("\t", ".tsv"),
    CSV(",", ".csv");

    private final String delimiter;
    private final String extension;

    FileFormat(String delimiter, String extension) {
        this.delimiter = delimiter;
        this.extension = extension;
    }

    public String getDelimiter() {
        return delimiter;
    }

    public String getExtension() {
        return extension;
    }
}