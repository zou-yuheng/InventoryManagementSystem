package zyhinventory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * API Client Class
 * Responsible for communicating with the remote server, fetching and submitting inventory data
 * Server address: http://10.122.18.142:38080
 *
 * Note: Switched to pure local mode, API code is commented out and retained.
 * Data is read from JSON files in the data/ directory (ZYHLocalData).
 * To restore remote API mode, uncomment the relevant code.
 */
public class ZYHApiClient {
    /** Data query interface URL */
    private static final String DATA_URL = "http://10.122.18.142:38080/data";

    /** Data modification interface URL */
    private static final String CHANGE_URL = "http://10.122.18.142:38080/change";

    /**
     * Query data (with full parameters)
     * @param type Data type (Product/Inventory/Inbound/Outbound)
     * @param filter Filter criteria (JSON format)
     * @param date Date filter
     * @param period Time period filter
     * @return JSON data returned by the server
     */
    public static String queryData(String type, String filter, String date, String period) {
        /* === Original API code - commented out, switched to local mode ===
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"type\":\"").append(type).append("\"");

        if (filter != null && !filter.isEmpty()) {
            jsonBuilder.append(",\"filter\":").append(filter);
        }

        if (date != null && !date.isEmpty()) {
            jsonBuilder.append(",\"date\":\"").append(date).append("\"");
        }

        if (period != null && !period.isEmpty()) {
            jsonBuilder.append(",\"period\":\"").append(period).append("\"");
        }

        jsonBuilder.append("}");

        return sendPostRequest(DATA_URL, jsonBuilder.toString());
        === End of original API code === */

        // Local mode: read directly from JSON files
        return ZYHLocalData.queryData(type);
    }

    /**
     * Query data (simple version)
     * @param type Data type
     * @return JSON data returned by the server
     */
    public static String queryData(String type) {
        /* === Original API code - commented out, switched to local mode ===
        return queryData(type, null, null, null);
        === End of original API code === */

        // Local mode: read directly from JSON files
        return ZYHLocalData.queryData(type);
    }

    /**
     * Modify inventory product information
     * @param action Operation type (add/modify/delete)
     * @param recordId Record ID
     * @param name Product name
     * @param barcode Product barcode
     * @param buyPrice Purchase price
     * @param sellPrice Sale price
     * @return JSON data returned by the server
     */
    public static String modifyInventory(String action, String recordId, String name,
            String barcode, Double buyPrice, Double sellPrice) {
        /* === Original API code - commented out, switched to read-only mode ===
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"type\":\"Inventory\",\"action\":\"").append(action).append("\"");

        if (recordId != null && !recordId.isEmpty()) {
            jsonBuilder.append(",\"record_id\":\"").append(recordId).append("\"");
        }

        if (barcode != null && !barcode.isEmpty()) {
            jsonBuilder.append(",\"Product Barcode\":\"").append(barcode).append("\"");
        }

        if (name != null && !name.isEmpty()) {
            jsonBuilder.append(",\"Product Name\":\"").append(name).append("\"");
        }

        if (buyPrice != null) {
            jsonBuilder.append(",\"Purchase Price\":").append(buyPrice);
        }

        if (sellPrice != null) {
            jsonBuilder.append(",\"Sale Price\":").append(sellPrice);
        }

        jsonBuilder.append("}");

        return sendPostRequest(CHANGE_URL, jsonBuilder.toString());
        === End of original API code === */

        // Read-only mode: returns success, operations only take effect in memory
        return "{\"success\":true,\"message\":\"Local read-only mode, operations only take effect in memory\"}";
    }

    /**
     * Modify inbound/outbound records
     * @param type Type (Inbound/Outbound)
     * @param action Operation type
     * @param recordId Record ID
     * @param barcode Product barcode
     * @param quantity Quantity
     * @param date Date
     * @param itemName Product name
     * @return JSON data returned by the server
     */
    public static String modifyStock(String type, String action, String recordId,
            String barcode, Integer quantity, String date, String itemName) {
        /* === Original API code - commented out, switched to read-only mode ===
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"type\":\"").append(type).append("\",\"action\":\"").append(action).append("\"");

        if (recordId != null && !recordId.isEmpty()) {
            jsonBuilder.append(",\"record_id\":\"").append(recordId).append("\"");
        }

        if (barcode != null && !barcode.isEmpty()) {
            jsonBuilder.append(",\"Product Barcode\":\"").append(barcode).append("\"");
        }

        if (quantity != null) {
            jsonBuilder.append(",\"Quantity\":").append(quantity);
        }

        if (date != null && !date.isEmpty()) {
            jsonBuilder.append(",\"Date\":\"").append(date).append("\"");
        }

        if (itemName != null && !itemName.isEmpty()) {
            if ("Inbound".equals(type)) {
                jsonBuilder.append(",\"Inbound Item\":\"").append(itemName).append("\"");
            } else if ("Outbound".equals(type)) {
                jsonBuilder.append(",\"Outbound Item\":\"").append(itemName).append("\"");
            }
        }

        jsonBuilder.append("}");

        return sendPostRequest(CHANGE_URL, jsonBuilder.toString());
        === End of original API code === */

        // Read-only mode: returns success, operations only take effect in memory
        return "{\"success\":true,\"message\":\"Local read-only mode, operations only take effect in memory\"}";
    }

    /**
     * Reset all data
     * @return JSON data returned by the server
     */
    public static String resetData() {
        /* === Original API code - commented out, switched to read-only mode ===
        return sendPostRequest(CHANGE_URL, "{\"action\":\"reset\"}");
        === End of original API code === */

        // Read-only mode: returns success
        return "{\"success\":true,\"message\":\"Local read-only mode, reset operation invalid\"}";
    }

    /**
     * Reset data by type
     * @param type Data type
     * @return JSON data returned by the server
     */
    public static String resetDataByType(String type) {
        /* === Original API code - commented out, switched to read-only mode ===
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\"type\":\"").append(type).append("\",\"action\":\"reset\"}");
        return sendPostRequest(CHANGE_URL, jsonBuilder.toString());
        === End of original API code === */

        // Read-only mode: returns success
        return "{\"success\":true,\"message\":\"Local read-only mode, reset operation invalid\"}";
    }

    /**
     * Send POST request to server
     * @param urlStr Request URL
     * @param jsonBody JSON format request body
     * @return Server response content
     */
    @SuppressWarnings("unused")
    private static String sendPostRequest(String urlStr, String jsonBody) {
        /* === Original API code - commented out, local mode does not use network requests ===
        try {
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Set request method to POST
            conn.setRequestMethod("POST");
            // Set request headers
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);  // Allow output
            conn.setConnectTimeout(10000);  // Connection timeout 10 seconds
            conn.setReadTimeout(10000);  // Read timeout 10 seconds

            // Write request body
            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes("UTF-8"));
            os.flush();
            os.close();

            // Get response code
            int responseCode = conn.getResponseCode();

            // Select input stream based on response code
            BufferedReader br;
            if (responseCode == 200) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream(), "UTF-8"));
            }

            // Read response content
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();
            conn.disconnect();

            return response.toString();
        } catch (Exception e) {
            // Return error JSON when exception occurs
            return "{\"error\":\"" + e.getMessage() + "\"}";
        }
        === End of original API code === */

        // Local mode: does not send network requests
        return "{\"error\":\"Local mode, network requests disabled\"}";
    }
}
