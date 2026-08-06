package zyhinventory;

/**
 * 商品实体类
 * 用于存储商品的基本信息，包括条码、名称、价格、库存等
 * 库存管理系统中的核心数据模型之一
 */
public class ZYHProduct {
    /** 记录ID，用于唯一标识每条商品记录 */
    private String recordId;

    /** 商品条码，用于扫码识别商品 */
    private String barcode;

    /** 商品名称 */
    private String name;

    /** 进货价格（采购价） */
    private double buyPrice;

    /** 销售价格（零售价） */
    private double sellPrice;

    /** 当前库存数量 */
    private int stockQuantity;

    /** 商品状态（正常/异常） */
    private String status;

    /** 最后更新时间 */
    private String lastUpdateTime;

    /**
     * 默认构造函数
     */
    public ZYHProduct() {
    }

    /**
     * 构造函数 - 创建新商品
     * @param barcode 商品条码
     * @param name 商品名称
     * @param buyPrice 进货价格
     * @param sellPrice 销售价格
     */
    public ZYHProduct(String barcode, String name, double buyPrice, double sellPrice) {
        this.barcode = barcode;
        this.name = name;
        this.buyPrice = buyPrice;
        this.sellPrice = sellPrice;
        this.stockQuantity = 0;  // 新商品初始库存为0
        this.status = "正常";
    }

    /**
     * 构造函数 - 创建完整商品记录
     * @param recordId 记录ID
     * @param barcode 商品条码
     * @param name 商品名称
     * @param buyPrice 进货价格
     * @param sellPrice 销售价格
     * @param stockQuantity 库存数量
     * @param status 商品状态
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
     * 获取进货价格
     * @return 进货价格
     */
    public double getBuyPrice() {
        return buyPrice;
    }

    /**
     * 设置进货价格
     * @param buyPrice 进货价格
     */
    public void setBuyPrice(double buyPrice) {
        this.buyPrice = buyPrice;
    }

    /**
     * 获取销售价格
     * @return 销售价格
     */
    public double getSellPrice() {
        return sellPrice;
    }

    /**
     * 设置销售价格
     * @param sellPrice 销售价格
     */
    public void setSellPrice(double sellPrice) {
        this.sellPrice = sellPrice;
    }

    /**
     * 获取当前库存数量
     * @return 库存数量
     */
    public int getStockQuantity() {
        return stockQuantity;
    }

    /**
     * 设置库存数量
     * 当库存数量小于0时自动标记为"异常"状态
     * @param stockQuantity 库存数量
     */
    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
        this.status = stockQuantity >= 0 ? "正常" : "异常";
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
     * 返回商品信息的字符串表示
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
     * 判断两个商品是否相同
     * 根据条码判断，条码相同即为同一商品
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ZYHProduct product = (ZYHProduct) obj;
        return barcode != null && barcode.equals(product.barcode);
    }

    /**
     * 根据条码生成哈希码
     */
    @Override
    public int hashCode() {
        return barcode != null ? barcode.hashCode() : 0;
    }
}
