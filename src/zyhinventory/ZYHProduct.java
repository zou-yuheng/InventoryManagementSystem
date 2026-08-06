package zyhinventory;

/**
 * Product Entity Class
 * Used to store basic product information, including barcode, name, price, stock, etc.
 * One of the core data models in the inventory management system
 */
public class ZYHProduct {
    /** Record ID, used to uniquely identify each product record */
    private String recordId;

    /** Product barcode, used for scanning to identify products */
    private String barcode;

    /** Product Name */
    private String name;

    /** Purchase Price (Cost) */
    private double buyPrice;

    /** Sale Price (Retail Price) */
    private double sellPrice;

    /** Current stock quantity */
    private int stockQuantity;

    /** Product status (Normal/Abnormal) */
    private String status;

    /** Last update time */
    private String lastUpdateTime;

    /**
     * Default constructor
     */
    public ZYHProduct() {
    }

    /**
     * Constructor - Create new product
     * @param barcode Product barcode
     * @param name Product name
     * @param buyPrice Purchase price
     * @param sellPrice Sale price
     */
    public ZYHProduct(String barcode, String name, double buyPrice, double sellPrice) {
        this.barcode = barcode;
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stockQuantity = 0;  // Initial stock of new product is 0
        this.status = "Normal";
    }

    /**
     * Constructor - Create complete product record
     * @param recordId Record ID
     * @param barcode Product barcode
     * @param name Product name
     * @param buyPrice Purchase price
     * @param sellPrice Sale price
     * @param stockQuantity Stock quantity
     * @param status Product status
     */
    public ZYHProduct(String recordId, String barcode, String name, double buyPrice,
            double sellPrice, int stockQuantity, String status) {
        this.recordId = recordId;
        this.barcode = barcode;
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stockQuantity = stockQuantity;
        this.status = status;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get purchase price
     * @return Purchase price
     */
    public double getBuyPrice() {
        return buyPrice;
    }

    /**
     * Set purchase price
     * @param buyPrice Purchase price
     */
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    /**
     * Get sale price
     * @return Sale price
     */
    public double getSellPrice() {
        return sellPrice;
    }

    /**
     * Set sale price
     * @param sellPrice Sale price
     */
    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    /**
     * Get current stock quantity
     * @return Stock quantity
     */
    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Set stock quantity
     * Automatically marks status as "Abnormal" when stock quantity is less than 0
     * @param stockQuantity Stock quantity
     */
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        this.status = stockQuantity >= 0 ? "Normal" : "Abnormal";
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * Returns string representation of product information
     */
    @Override
    public String toString() {
        return "ZYHProduct{" +
                "recordId='" + recordId + '\'' +
                ", barcode='" + barcode + '\'' +
                ", name='" + name + '\'' +
                ", buyPrice=" + buyPrice +
                ", sellPrice=" + sellPrice +
                ", stockQuantity=" + stockQuantity +
                ", status='" + status + '\'' +
                '}';
    }

    /**
     * Determine whether two products are the same
     * Judged by barcode, same barcode means same product
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ZYHProduct product = (ZYHProduct) obj;
        return barcode != null && barcode.equals(product.barcode);
    }

    /**
     * Generate hash code based on barcode
     */
    @Override
    public int hashCode() {
        return barcode != null ? barcode.hashCode() : 0;
    }
}
