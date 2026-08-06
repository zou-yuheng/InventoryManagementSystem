package zyhinventory;

/**
 * Data Manager Class
 * Unified management of all data in the inventory system
 * Uses sequential list to store product list and inventory list
 * Uses linked list to store inbound queue, outbound stack and transaction records
 */
public class ZYHDataManager {
    /** Product list (Sequential list) - Stores basic product information */
    private ZYHSequentialList<ZYHProduct> productList;

    /** Inventory list (Sequential list) - Stores inventory product information */
    private ZYHSequentialList<ZYHProduct> inventoryList;

    /** Inbound queue (Linked list) - FIFO, stores inbound records */
    private ZYHLinkedList<ZYHInboundRecord> inboundQueue;

    /** Outbound stack (Linked list) - LIFO, stores outbound records */
    private ZYHLinkedList<ZYHOutboundRecord> outboundStack;

    /** Transaction records (Linked list) - Stores all inventory change records */
    private ZYHLinkedList<ZYHTransactionRecord> transactionFlow;

    /**
     * Default constructor
     * Initialize all data containers
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
     * Add product to product list
     * @param product Product object
     */
    public void addProduct(ZYHProduct product) {
        productList.add(product);
    }

    /**
     * Add product to inventory list
     * @param product Inventory product object
     */
    public void addInventory(ZYHProduct product) {
        inventoryList.add(product);
    }

    /**
     * Enqueue inbound record (add to queue tail)
     * @param record Inbound record
     */
    public void enqueueInbound(ZYHInboundRecord record) {
        inboundQueue.addLast(record);
    }

    /**
     * Dequeue inbound record (remove from queue head)
     * @return Removed inbound record
     */
    public ZYHInboundRecord dequeueInbound() {
        return inboundQueue.removeFirst();
    }

    /**
     * Push outbound record (add to stack top)
     * @param record Outbound record
     */
    public void pushOutbound(ZYHOutboundRecord record) {
        outboundStack.addLast(record);
    }

    /**
     * Pop outbound record (remove from stack top)
     * @return Removed outbound record
     */
    public ZYHOutboundRecord popOutbound() {
        return outboundStack.removeLast();
    }

    /**
     * Add transaction record
     * @param record Transaction record
     */
    public void addTransaction(ZYHTransactionRecord record) {
        transactionFlow.addLast(record);
    }

    /**
     * Remove transaction record by record ID
     * @param recordId Record ID
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
     * Find product by barcode
     * @param barcode Product barcode
     * @return Found product, returns null if not found
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
     * Find inventory product by barcode
     * @param barcode Product barcode
     * @return Found inventory product, returns null if not found
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
     * Get product count
     * @return Number of products in product list
     */
    public int getProductCount() {
        return productList.size();
    }

    /**
     * Get inventory product count
     * @return Number of products in inventory list
     */
    public int getInventoryCount() {
        return inventoryList.size();
    }

    /**
     * Get inbound record count
     * @return Number of records in inbound queue
     */
    public int getInboundCount() {
        return inboundQueue.size();
    }

    /**
     * Get outbound record count
     * @return Number of records in outbound stack
     */
    public int getOutboundCount() {
        return outboundStack.size();
    }

    /**
     * Get transaction record count
     * @return Number of records in transaction linked list
     */
    public int getTransactionCount() {
        return transactionFlow.size();
    }

    /**
     * Clear all data
     */
    public void clear() {
        productList.clear();
        inventoryList.clear();
        inboundQueue.clear();
        outboundStack.clear();
        transactionFlow.clear();
    }

    /**
     * Performance Comparison Result Class
     * Used to store performance comparison data of sequential list and linked list in add/delete/query/update operations
     */
    public static class PerformanceComparison {
        /** Sequential list add time */
        private long sequentialAddTime;
        /** Linked list add time */
        private long linkedListAddTime;
        /** Sequential list remove time */
        private long sequentialRemoveTime;
        /** Linked list remove time */
        private long linkedListRemoveTime;
        /** Sequential list search time */
        private long sequentialSearchTime;
        /** Linked list search time */
        private long linkedListSearchTime;
        /** Sequential list update time */
        private long sequentialUpdateTime;
        /** Linked list update time */
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
         * Returns string representation of performance comparison result
         */
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("========== Sequential List vs Linked List Performance Comparison ==========\n\n");

            sb.append("[Add Operation]\n");
            sb.append("Sequential List: ").append(sequentialAddTime).append(" ns\n");
            sb.append("Linked List:     ").append(linkedListAddTime).append(" ns\n");
            String addWinner = sequentialAddTime < linkedListAddTime ? "Sequential List" : "Linked List";
            sb.append("Conclusion: ").append(addWinner).append(" is faster\n\n");

            sb.append("[Delete Operation]\n");
            sb.append("Sequential List: ").append(sequentialRemoveTime).append(" ns\n");
            sb.append("Linked List:     ").append(linkedListRemoveTime).append(" ns\n");
            String removeWinner = sequentialRemoveTime < linkedListRemoveTime ? "Sequential List" : "Linked List";
            sb.append("Conclusion: ").append(removeWinner).append(" is faster\n\n");

            sb.append("[Search Operation]\n");
            sb.append("Sequential List: ").append(sequentialSearchTime).append(" ns\n");
            sb.append("Linked List:     ").append(linkedListSearchTime).append(" ns\n");
            String searchWinner = sequentialSearchTime < linkedListSearchTime ? "Sequential List" : "Linked List";
            sb.append("Conclusion: ").append(searchWinner).append(" is faster\n\n");

            sb.append("[Update Operation]\n");
            sb.append("Sequential List: ").append(sequentialUpdateTime).append(" ns\n");
            sb.append("Linked List:     ").append(linkedListUpdateTime).append(" ns\n");
            String updateWinner = sequentialUpdateTime < linkedListUpdateTime ? "Sequential List" : "Linked List";
            sb.append("Conclusion: ").append(updateWinner).append(" is faster\n\n");

            sb.append("================================================================");
            return sb.toString();
        }
    }

    /**
     * Execute performance comparison test
     * @param dataSize Test data size
     * @return Performance comparison result
     */
    public PerformanceComparison comparePerformance(int dataSize) {
        PerformanceComparison result = new PerformanceComparison();

        ZYHSequentialList<Integer> seqList = new ZYHSequentialList<>();
        ZYHLinkedList<Integer> linkedList = new ZYHLinkedList<>();

        long startTime, endTime;

        // Test sequential list add
        startTime = System.nanoTime();
        for (int i = 0; i < dataSize; i++) {
            seqList.add(i);
        }
        endTime = System.nanoTime();
        result.setSequentialAddTime(endTime - startTime);

        // Test linked list add
        startTime = System.nanoTime();
        for (int i = 0; i < dataSize; i++) {
            linkedList.addLast(i);
        }
        endTime = System.nanoTime();
        result.setLinkedListAddTime(endTime - startTime);

        // Test middle position deletion
        int midIndex = dataSize / 2;
        startTime = System.nanoTime();
        seqList.remove(midIndex);
        endTime = System.nanoTime();
        result.setSequentialRemoveTime(endTime - startTime);

        startTime = System.nanoTime();
        linkedList.remove(midIndex);
        endTime = System.nanoTime();
        result.setLinkedListRemoveTime(endTime - startTime);

        // Test search operation
        int targetValue = dataSize - 1;
        startTime = System.nanoTime();
        seqList.indexOf(targetValue);
        endTime = System.nanoTime();
        result.setSequentialSearchTime(endTime - startTime);

        startTime = System.nanoTime();
        linkedList.indexOf(targetValue);
        endTime = System.nanoTime();
        result.setLinkedListSearchTime(endTime - startTime);

        // Test update operation
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
