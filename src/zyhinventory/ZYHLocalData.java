package zyhinventory;

import java.io.*;
import java.nio.file.*;

/**
 * Local Data Reader
 * Reads JSON data files from the data/ directory, replacing the remote API
 */
public class ZYHLocalData {

    public static String queryData(String type) {
        String fileName;
        switch (type) {
            case "Inventory":
                fileName = "data/inventory.json";
                break;
            case "Inbound":
                fileName = "data/inbound.json";
                break;
            case "Outbound":
                fileName = "data/outbound.json";
                break;
            default:
                return "{\"error\":\"Unknown data type\"}";
        }

        try {
            Path path = Paths.get(fileName);
            if (Files.exists(path)) {
                return new String(Files.readAllBytes(path), "UTF-8");
            } else {
                return "{\"error\":\"Data file does not exist\"}";
            }
        } catch (Exception e) {
            return "{\"error\":\"Read failed\"}";
        }
    }
}
