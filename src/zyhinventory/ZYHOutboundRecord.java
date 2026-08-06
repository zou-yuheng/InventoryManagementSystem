package zyhinventory;

/**
 * Outbound Record Entity Class
 * Used to store information related to product outbound operations
 * Outbound records are stored in a linked list structure (Stack, LIFO)
 */
public class ZYHOutboundRecord {
    /** Record ID, uniquely identifies each outbound record */
    private String recordId;

    /** Product barcode */
    private String barcode;

    /** Product name */
    private String itemName;

    /** Outbound quantity */
    private int quantity;

    /** Outbound date (format: YYYY-MM-DD) */
    private String date;

    /** Outbound time (complete timestamp) */
    private String outboundTime;

    /**
     * Default constructor
     */
    public ZYHOutboundRecord() {
    }

    /**
     * Constructor - Create new outbound record
     * @param barcode Product barcode
     * @param itemName Product name
     * @param quantity Outbound quantity
     */
    public ZYHOutboundRecord(String barcode, String itemName, int quantity) {
        this.barcode = barcode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.date = java.time.LocalDate.now().toString();  // Automatically set current date
        this.outboundTime = java.time.LocalDateTime.now().toString().substring(0, 19);  // Automatically set current time
    }

    /**
     * Constructor - Create outbound record with specified date
     * @param recordId Record ID
     * @param barcode Product barcode
     * @param itemName Product name
     * @param quantity Outbound quantity
     * @param date Outbound date
     */
    public ZYHOutboundRecord(String recordId, String barcode, String itemName,
            int quantity, String date) {
        this.recordId = recordId;
        this.barcode = barcode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.date = date;
        this.outboundTime = java.time.LocalDateTime.now().toString().substring(0, 19);
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    /**
     * Get outbound quantity
     * @return Outbound quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Set outbound quantity
     * @param quantity Outbound quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getOutboundTime() {
        return outboundTime;
    }

    public void setOutboundTime(String outboundTime) {
        this.outboundTime = outboundTime;
    }

    /**
     * Returns string representation of outbound record information
     */
    @Override
    public String toString() {
        return "ZYHOutboundRecord{" +
                "recordId='" + recordId + '\'' +
                ", barcode='" + barcode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", date='" + date + '\'' +
                ", outboundTime='" + outboundTime + '\'' +
                '}';
    }

    /**
     * Determine whether two outbound records are the same
     * Judged by record ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ZYHOutboundRecord record = (ZYHOutboundRecord) obj;
        return recordId != null && recordId.equals(record.recordId);
    }

    /**
     * Generate hash code based on record ID
     */
    @Override
    public int hashCode() {
        return recordId != null ? recordId.hashCode() : 0;
    }
}
