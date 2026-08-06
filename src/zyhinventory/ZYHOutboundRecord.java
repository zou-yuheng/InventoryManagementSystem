package zyhinventory;

/**
 * 出库记录实体类
 * 用于存储商品出库操作的相关信息
 * 出库记录采用链表结构存储（栈，先进后出）
 */
public class ZYHOutboundRecord {
    /** 记录ID，唯一标识每条出库记录 */
    private String recordId;

    /** 商品条码 */
    private String barcode;

    /** 商品名称 */
    private String itemName;

    /** 出库数量 */
    private int quantity;

    /** 出库日期（格式：YYYY-MM-DD） */
    private String date;

    /** 出库时间（完整时间戳） */
    private String outboundTime;

    /**
     * 默认构造函数
     */
    public ZYHOutboundRecord() {
    }

    /**
     * 构造函数 - 创建新出库记录
     * @param barcode 商品条码
     * @param itemName 商品名称
     * @param quantity 出库数量
     */
    public ZYHOutboundRecord(String barcode, String itemName, int quantity) {
        this.barcode = barcode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.date = java.time.LocalDate.now().toString();  // 自动设置当前日期
        this.outboundTime = java.time.LocalDateTime.now().toString().substring(0, 19);  // 自动设置当前时间
    }

    /**
     * 构造函数 - 创建指定日期的出库记录
     * @param recordId 记录ID
     * @param barcode 商品条码
     * @param itemName 商品名称
     * @param quantity 出库数量
     * @param date 出库日期
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
     * 获取出库数量
     * @return 出库数量
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * 设置出库数量
     * @param quantity 出库数量
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
     * 返回出库记录信息的字符串表示
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
     * 判断两条出库记录是否相同
     * 根据记录ID判断
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ZYHOutboundRecord record = (ZYHOutboundRecord) obj;
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
