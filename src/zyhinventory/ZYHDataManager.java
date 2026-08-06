package zyhinventory;

/**
 * 数据管理器类
 * 统一管理库存系统中的所有数据
 * 使用顺序表存储商品列表和库存列表
 * 使用链表存储入库队列、出库栈和流水记录
 */
public class ZYHDataManager {
    /** 商品列表（顺序表） - 存储商品基本信息 */
    private ZYHSequentialList<ZYHProduct> productList;

    /** 库存列表（顺序表） - 存储库存商品信息 */
    private ZYHSequentialList<ZYHProduct> inventoryList;

    /** 入库队列（链表） - 先进先出，存储入库记录 */
    private ZYHLinkedList<ZYHInboundRecord> inboundQueue;

    /** 出库栈（链表） - 先进后出，存储出库记录 */
    private ZYHLinkedList<ZYHOutboundRecord> outboundStack;

    /** 流水记录（链表） - 存储所有库存变动记录 */
    private ZYHLinkedList<ZYHTransactionRecord> transactionFlow;

    /**
     * 默认构造函数
     * 初始化所有数据容器
     */
    public ZYHDataManager() {
        this.productList = new ZYHSequentialList<>();
        this.inventoryList = new ZYHSequentialList<>();
        this.inboundQueue = new ZYHLinkedList<>();
        this.outboundStack = new ZYHLinkedList<>();
        this.transactionFlow = new ZYHLinkedList<>();
    }

    public ZYHSequentialList<ZYHProduct> getProductList() {
        return productList;
    }

    public ZYHSequentialList<ZYHProduct> getInventoryList() {
        return inventoryList;
    }

    public ZYHLinkedList<ZYHInboundRecord> getInboundQueue() {
        return inboundQueue;
    }

    public ZYHLinkedList<ZYHOutboundRecord> getOutboundStack() {
        return outboundStack;
    }

    public ZYHLinkedList<ZYHTransactionRecord> getTransactionFlow() {
        return transactionFlow;
    }

    /**
     * 添加商品到商品列表
     * @param product 商品对象
     */
    public void addProduct(ZYHProduct product) {
        productList.add(product);
    }

    /**
     * 添加商品到库存列表
     * @param product 库存商品对象
     */
    public void addInventory(ZYHProduct product) {
        inventoryList.add(product);
    }

    /**
     * 入库记录入队（添加到队列尾部）
     * @param record 入库记录
     */
    public void enqueueInbound(ZYHInboundRecord record) {
        inboundQueue.addLast(record);
    }

    /**
     * 入库记录出队（从队列头部移除）
     * @return 被移除的入库记录
     */
    public ZYHInboundRecord dequeueInbound() {
        return inboundQueue.removeFirst();
    }

    /**
     * 出库记录入栈（添加到栈顶）
     * @param record 出库记录
     */
    public void pushOutbound(ZYHOutboundRecord record) {
        outboundStack.addLast(record);
    }

    /**
     * 出库记录出栈（从栈顶移除）
     * @return 被移除的出库记录
     */
    public ZYHOutboundRecord popOutbound() {
        return outboundStack.removeLast();
    }

    /**
     * 添加流水记录
     * @param record 流水记录
     */
    public void addTransaction(ZYHTransactionRecord record) {
        transactionFlow.addLast(record);
    }

    /**
     * 根据记录ID移除流水记录
     * @param recordId 记录ID
     */
    public void removeTransaction(String recordId) {
        for (ZYHTransactionRecord record : transactionFlow) {
            if (record.getRecordId() != null && record.getRecordId().equals(recordId)) {
                transactionFlow.remove(record);
                break;
            }
        }
    }

    /**
     * 根据条码查找商品
     * @param barcode 商品条码
     * @return 找到的商品，未找到返回null
     */
    public ZYHProduct findProductByBarcode(String barcode) {
        for (int i = 0; i < productList.size(); i++) {
            ZYHProduct p = productList.get(i);
            if (p.getBarcode() != null && p.getBarcode().equals(barcode)) {
                return p;
            }
        }
        return null;
    }

    /**
     * 根据条码查找库存商品
     * @param barcode 商品条码
     * @return 找到的库存商品，未找到返回null
     */
    public ZYHProduct findInventoryByBarcode(String barcode) {
        for (int i = 0; i < inventoryList.size(); i++) {
            ZYHProduct p = inventoryList.get(i);
            if (p.getBarcode() != null && p.getBarcode().equals(barcode)) {
                return p;
            }
        }
        return null;
    }

    /**
     * 获取商品数量
     * @return 商品列表中的商品数量
     */
    public int getProductCount() {
        return productList.size();
    }

    /**
     * 获取库存商品数量
     * @return 库存列表中的商品数量
     */
    public int getInventoryCount() {
        return inventoryList.size();
    }

    /**
     * 获取入库记录数量
     * @return 入库队列中的记录数量
     */
    public int getInboundCount() {
        return inboundQueue.size();
    }

    /**
     * 获取出库记录数量
     * @return 出库栈中的记录数量
     */
    public int getOutboundCount() {
        return outboundStack.size();
    }

    /**
     * 获取流水记录数量
     * @return 流水链表中的记录数量
     */
    public int getTransactionCount() {
        return transactionFlow.size();
    }

    /**
     * 清空所有数据
     */
    public void clear() {
        productList.clear();
        inventoryList.clear();
        inboundQueue.clear();
        outboundStack.clear();
        transactionFlow.clear();
    }

    /**
     * 性能对比结果类
     * 用于存储顺序表和链表在增删改查操作上的性能对比数据
     */
    public static class PerformanceComparison {
        /** 顺序表添加耗时 */
        private long sequentialAddTime;
        /** 链表添加耗时 */
        private long linkedListAddTime;
        /** 顺序表删除耗时 */
        private long sequentialRemoveTime;
        /** 链表删除耗时 */
        private long linkedListRemoveTime;
        /** 顺序表查询耗时 */
        private long sequentialSearchTime;
        /** 链表查询耗时 */
        private long linkedListSearchTime;
        /** 顺序表修改耗时 */
        private long sequentialUpdateTime;
        /** 链表修改耗时 */
        private long linkedListUpdateTime;

        public PerformanceComparison() {
        }

        public long getSequentialAddTime() {
            return sequentialAddTime;
        }

        public void setSequentialAddTime(long sequentialAddTime) {
            this.sequentialAddTime = sequentialAddTime;
        }

        public long getLinkedListAddTime() {
            return linkedListAddTime;
        }

        public void setLinkedListAddTime(long linkedListAddTime) {
            this.linkedListAddTime = linkedListAddTime;
        }

        public long getSequentialRemoveTime() {
            return sequentialRemoveTime;
        }

        public void setSequentialRemoveTime(long sequentialRemoveTime) {
            this.sequentialRemoveTime = sequentialRemoveTime;
        }

        public long getLinkedListRemoveTime() {
            return linkedListRemoveTime;
        }

        public void setLinkedListRemoveTime(long linkedListRemoveTime) {
            this.linkedListRemoveTime = linkedListRemoveTime;
        }

        public long getSequentialSearchTime() {
            return sequentialSearchTime;
        }

        public void setSequentialSearchTime(long sequentialSearchTime) {
            this.sequentialSearchTime = sequentialSearchTime;
        }

        public long getLinkedListSearchTime() {
            return linkedListSearchTime;
        }

        public void setLinkedListSearchTime(long linkedListSearchTime) {
            this.linkedListSearchTime = linkedListSearchTime;
        }

        public long getSequentialUpdateTime() {
            return sequentialUpdateTime;
        }

        public void setSequentialUpdateTime(long sequentialUpdateTime) {
            this.sequentialUpdateTime = sequentialUpdateTime;
        }

        public long getLinkedListUpdateTime() {
            return linkedListUpdateTime;
        }

        public void setLinkedListUpdateTime(long linkedListUpdateTime) {
            this.linkedListUpdateTime = linkedListUpdateTime;
        }

        /**
         * 返回性能对比结果的字符串表示
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("========== 顺序表 vs 链表 性能比对 ==========\n\n");

            sb.append("【添加操作】\n");
            sb.append("顺序表: ").append(sequentialAddTime).append(" ns\n");
            sb.append("链表:   ").append(linkedListAddTime).append(" ns\n");
            String addWinner = sequentialAddTime < linkedListAddTime ? "顺序表" : "链表";
            sb.append("结论: ").append(addWinner).append(" 更快\n\n");

            sb.append("【删除操作】\n");
            sb.append("顺序表: ").append(sequentialRemoveTime).append(" ns\n");
            sb.append("链表:   ").append(linkedListRemoveTime).append(" ns\n");
            String removeWinner = sequentialRemoveTime < linkedListRemoveTime ? "顺序表" : "链表";
            sb.append("结论: ").append(removeWinner).append(" 更快\n\n");

            sb.append("【查询操作】\n");
            sb.append("顺序表: ").append(sequentialSearchTime).append(" ns\n");
            sb.append("链表:   ").append(linkedListSearchTime).append(" ns\n");
            String searchWinner = sequentialSearchTime < linkedListSearchTime ? "顺序表" : "链表";
            sb.append("结论: ").append(searchWinner).append(" 更快\n\n");

            sb.append("【修改操作】\n");
            sb.append("顺序表: ").append(sequentialUpdateTime).append(" ns\n");
            sb.append("链表:   ").append(linkedListUpdateTime).append(" ns\n");
            String updateWinner = sequentialUpdateTime < linkedListUpdateTime ? "顺序表" : "链表";
            sb.append("结论: ").append(updateWinner).append(" 更快\n\n");

            sb.append("===========================================");
            return sb.toString();
        }
    }

    /**
     * 执行性能对比测试
     * @param dataSize 测试数据规模
     * @return 性能对比结果
     */
    public PerformanceComparison comparePerformance(int dataSize) {
        PerformanceComparison result = new PerformanceComparison();

        ZYHSequentialList<Integer> seqList = new ZYHSequentialList<>();
        ZYHLinkedList<Integer> linkedList = new ZYHLinkedList<>();

        long startTime, endTime;

        // 测试顺序表添加
        startTime = System.nanoTime();
        for (int i = 0; i < dataSize; i++) {
            seqList.add(i);
        }
        endTime = System.nanoTime();
        result.setSequentialAddTime(endTime - startTime);

        // 测试链表添加
        startTime = System.nanoTime();
        for (int i = 0; i < dataSize; i++) {
            linkedList.addLast(i);
        }
        endTime = System.nanoTime();
        result.setLinkedListAddTime(endTime - startTime);

        // 测试中间位置删除
        int midIndex = dataSize / 2;
        startTime = System.nanoTime();
        seqList.remove(midIndex);
        endTime = System.nanoTime();
        result.setSequentialRemoveTime(endTime - startTime);

        startTime = System.nanoTime();
        linkedList.remove(midIndex);
        endTime = System.nanoTime();
        result.setLinkedListRemoveTime(endTime - startTime);

        // 测试查询操作
        int targetValue = dataSize - 1;
        startTime = System.nanoTime();
        seqList.indexOf(targetValue);
        endTime = System.nanoTime();
        result.setSequentialSearchTime(endTime - startTime);

        startTime = System.nanoTime();
        linkedList.indexOf(targetValue);
        endTime = System.nanoTime();
        result.setLinkedListSearchTime(endTime - startTime);

        // 测试修改操作
        startTime = System.nanoTime();
        seqList.set(midIndex, dataSize + 1);
        endTime = System.nanoTime();
        result.setSequentialUpdateTime(endTime - startTime);

        startTime = System.nanoTime();
        linkedList.set(midIndex, dataSize + 1);
        endTime = System.nanoTime();
        result.setLinkedListUpdateTime(endTime - startTime);

        return result;
    }
}
