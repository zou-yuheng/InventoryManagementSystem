package zyhinventory;

/**
 * 流水记录实体类
 * 用于存储所有库存变动的流水信息
 * 流水记录采用链表结构存储，支持频繁的增删操作
 */
public class ZYHTransactionRecord {
    /** 记录ID，唯一标识每条流水记录 */
    private String recordId;

    /** 商品条码 */
    private String barcode;

    /** 商品名称 */
    private String itemName;

    /** 交易类型（入库/出库） */
    private String transactionType;

    /** 交易数量 */
    private int quantity;

    /** 交易时间 */
    private String dateTime;

    /** 交易描述 */
    private String description;

    /**
     * 默认构造函数
     */
    public ZYHTransactionRecord() {
    }

    /**
     * 构造函数 - 创建新流水记录
     * @param barcode 商品条码
     * @param itemName 商品名称
     * @param transactionType 交易类型（入库/出库）
     * @param quantity 交易数量
     * @param description 交易描述
     */
    public ZYHTransactionRecord(String barcode, String itemName, String transactionType,
            int quantity, String description) {
        this.barcode = barcode;
        this.itemName = itemName;
        this.transactionType = transactionType;
        this.quantity = quantity;
        this.description = description;
        this.dateTime = java.time.LocalDateTime.now().toString().substring(0, 19);  // 自动设置当前时间
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
     * 获取交易类型
     * @return 交易类型（入库/出库）
     */
    public String getTransactionType() {
        return transactionType;
    }

    /**
     * 设置交易类型
     * @param transactionType 交易类型
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    /**
     * 获取交易数量
     * @return 交易数量
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * 设置交易数量
     * @param quantity 交易数量
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
     * 返回流水记录信息的字符串表示
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
     * 判断两条流水记录是否相同
     * 根据记录ID判断
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ZYHTransactionRecord record = (ZYHTransactionRecord) obj;
        return recordId != null && recordId.equals(record.recordId);
    }

    /**
     * 根据记录ID生成哈希码
     */
    @Override
    public int hashCode() {
        return recordId != null ? recordId.hashCode() : 0;
    }
}
