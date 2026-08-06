package zyhinventory;

/**
 * Transaction Record Entity Class
 * Used to store transaction information for all inventory changes
 * Transaction records are stored in a linked list structure, supporting frequent add/delete operations
 */
public class ZYHTransactionRecord {
    /** Record ID, uniquely identifies each transaction record */
    private String recordId;

    /** Product barcode */
    private String barcode;

    /** Product name */
    private String itemName;

    /** Transaction type (Inbound/Outbound) */
    private String transactionType;

    /** Transaction quantity */
    private int quantity;

    /** Transaction time */
    private String dateTime;

    /** Transaction description */
    private String description;

    /**
     * Default constructor
     */
    public ZYHTransactionRecord() {
    }

    /**
     * Constructor - Create new transaction record
     * @param barcode Product barcode
     * @param itemName Product name
     * @param transactionType Transaction type (Inbound/Outbound)
     * @param quantity Transaction quantity
     * @param description Transaction description
     */
    public ZYHTransactionRecord(String barcode, String itemName, String transactionType,
            int quantity, String description) {
        this.barcode = barcode;
        this.itemName = itemName;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.description = description;
        this.dateTime = java.time.LocalDateTime.now().toString().substring(0, 19);  // Automatically set current time
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
     * Get transaction type
     * @return Transaction type (Inbound/Outbound)
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * Set transaction type
     * @param transactionType Transaction type
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * Get transaction quantity
     * @return Transaction quantity
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Set transaction quantity
     * @param quantity Transaction quantity
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns string representation of transaction record information
     */
    @Override
    public String toString() {
        return "ZYHTransactionRecord{" +
                "recordId='" + recordId + '\'' +
                ", barcode='" + barcode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", quantity=" + quantity +
                ", dateTime='" + dateTime + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

    /**
     * Determine whether two transaction records are the same
     * Judged by record ID
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ZYHTransactionRecord record = (ZYHTransactionRecord) obj;
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
