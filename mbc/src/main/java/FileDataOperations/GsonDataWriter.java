package FileDataOperations;

import Interfaces.IDataWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Implementation of IDataWriter that serializes objects to JSON using Gson.
 */
public class GsonDataWriter implements IDataWriter {

    private final Gson gson;

    public GsonDataWriter() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    @Override
    public void writeData(Object data, String filePath) {
        try {
            String jsonOutput = this.gson.toJson(data);
            Files.writeString(Paths.get(filePath), jsonOutput);
        } catch (IOException e) {
            System.out.println("Error writing JSON file to " + filePath + ": " + e.getMessage());
        }
    }
}
