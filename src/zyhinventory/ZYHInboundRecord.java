package zyhinventory;

/**
 * 入库记录实体类
 * 用于存储商品入库操作的相关信息
 * 入库记录采用链表结构存储（队列，先进先出）
 */
public class ZYHInboundRecord {
    /** 记录ID，唯一标识每条入库记录 */
    private String recordId;

    /** 商品条码 */
    private String barcode;

    /** 商品名称 */
    private String itemName;

    /** 入库数量 */
    private int quantity;

    /** 入库日期（格式：YYYY-MM-DD） */
    private String date;

    /** 入库时间（完整时间戳） */
    private String inboundTime;

    /**
     * 默认构造函数
     */
    public ZYHInboundRecord() {
    }

    /**
     * 构造函数 - 创建新入库记录
     * @param barcode 商品条码
     * @param itemName 商品名称
     * @param quantity 入库数量
     */
    public ZYHInboundRecord(String barcode, String itemName, int quantity) {
        this.barcode = barcode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.date = java.time.LocalDate.now().toString();  // 自动设置当前日期
        this.inboundTime = java.time.LocalDateTime.now().toString().substring(0, 19);  // 自动设置当前时间
    }

    /**
     * 构造函数 - 创建指定日期的入库记录
     * @param recordId 记录ID
     * @param barcode 商品条码
     * @param itemName 商品名称
     * @param quantity 入库数量
     * @param date 入库日期
     */
    public ZYHInboundRecord(String recordId, String barcode, String itemName,
            int quantity, String date) {
        this.recordId = recordId;
        this.barcode = barcode;
        this.itemName = itemName;
        this.quantity = quantity;
        this.date = date;
        this.inboundTime = java.time.LocalDateTime.now().toString().substring(0, 19);
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
     * 获取入库数量
     * @return 入库数量
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * 设置入库数量
     * @param quantity 入库数量
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

    public String getInboundTime() {
        return inboundTime;
    }

    public void setInboundTime(String inboundTime) {
        this.inboundTime = inboundTime;
    }

    /**
     * 返回入库记录信息的字符串表示
     */
    @Override
    public String toString() {
        return "ZYHInboundRecord{" +
                "recordId='" + recordId + '\'' +
                ", barcode='" + barcode + '\'' +
                ", itemName='" + itemName + '\'' +
                ", quantity=" + quantity +
                ", date='" + date + '\'' +
                ", inboundTime='" + inboundTime + '\'' +
                '}';
    }

    /**
     * 判断两条入库记录是否相同
     * 根据记录ID判断
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ZYHInboundRecord record = (ZYHInboundRecord) obj;
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
