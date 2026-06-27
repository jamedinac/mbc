package Interfaces;

/**
 * Interface defining a contract for writing generic data objects to a file.
 */
public interface IDataWriter {
    /**
     * Serializes and writes the given data object to the specified file path.
     *
     * @param data     The object/map to serialize.
     * @param filePath The destination file path.
     */
    void writeData(Object data, String filePath);
}
