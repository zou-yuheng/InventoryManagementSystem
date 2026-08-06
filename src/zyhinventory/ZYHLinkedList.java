package zyhinventory;

import java.util.Iterator;

/**
 * 双向链表实现
 * 一种基于节点连接的数据结构，支持高效的插入和删除操作
 * 用于存储入库记录队列、出库记录栈、流水记录等需要频繁增删的数据
 * @param <T> 泛型类型
 */
public class ZYHLinkedList<T> implements Iterable<T> {
    /** 头节点 */
    private Node<T> head;

    /** 尾节点 */
    private Node<T> tail;

    /** 当前元素数量 */
    private int size;

    /**
     * 节点内部类
     * 包含数据域和前后指针
     */
    private static class Node<T> {
        /** 节点数据 */
        T data;
        /** 指向下一个节点 */
        Node<T> next;
        /** 指向前一个节点 */
        Node<T> prev;

        Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    /**
     * 默认构造函数
     */
    public ZYHLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * 在链表头部添加元素
     * 时间复杂度：O(1)
     * @param element 要添加的元素
     */
    public void addFirst(T element) {
        Node<T> newNode = new Node<>(element);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    /**
     * 在链表尾部添加元素
     * 时间复杂度：O(1)
     * @param element 要添加的元素
     */
    public void addLast(T element) {
        Node<T> newNode = new Node<>(element);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    /**
     * 在指定位置插入元素
     * 时间复杂度：O(n)
     * @param index 插入位置
     * @param element 要插入的元素
     */
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("索引越界: " + index);
        }
        if (index == 0) {
            addFirst(element);
        } else if (index == size) {
            addLast(element);
        } else {
            Node<T> newNode = new Node<>(element);
            Node<T> current = getNode(index);
            newNode.next = current;
            newNode.prev = current.prev;
            current.prev.next = newNode;
            current.prev = newNode;
            size++;
        }
    }

    /**
     * 移除头部元素
     * 时间复杂度：O(1)
     * @return 被移除的元素
     */
    public T removeFirst() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("链表为空");
        }
        T result = head.data;
        if (head == tail) {
            head = tail = null;
        } else {
            head = head.next;
            head.prev = null;
        }
        size--;
        return result;
    }

    /**
     * 移除尾部元素
     * 时间复杂度：O(1)
     * @return 被移除的元素
     */
    public T removeLast() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("链表为空");
        }
        T result = tail.data;
        if (head == tail) {
            head = tail = null;
        } else {
            tail = tail.prev;
            tail.next = null;
        }
        size--;
        return result;
    }

    /**
     * 移除指定位置的元素
     * 时间复杂度：O(n)
     * @param index 元素位置
     * @return 被移除的元素
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("索引越界: " + index);
        }
        Node<T> current = getNode(index);
        T result = current.data;

        if (current.prev != null) {
            current.prev.next = current.next;
        } else {
            head = current.next;
        }

        if (current.next != null) {
            current.next.prev = current.prev;
        } else {
            tail = current.prev;
        }

        size--;
        return result;
    }

    /**
     * 移除指定元素
     * 时间复杂度：O(n)
     * @param element 要移除的元素
     * @return 是否移除成功
     */
    public boolean remove(T element) {
        Node<T> current = head;
        while (current != null) {
            if (current.data.equals(element)) {
                if (current.prev != null) {
                    current.prev.next = current.next;
                } else {
                    head = current.next;
                }
                if (current.next != null) {
                    current.next.prev = current.prev;
                } else {
                    tail = current.prev;
                }
                size--;
                return true;
            }
            current = current.next;
        }
        return false;
    }

    /**
     * 获取头部元素
     * 时间复杂度：O(1)
     * @return 头部元素
     */
    public T getFirst() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("链表为空");
        }
        return head.data;
    }

    /**
     * 获取尾部元素
     * 时间复杂度：O(1)
     * @return 尾部元素
     */
    public T getLast() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("链表为空");
        }
        return tail.data;
    }

    /**
     * 获取指定位置的元素
     * 时间复杂度：O(n)
     * @param index 元素位置
     * @return 指定位置的元素
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("索引越界: " + index);
        }
        return getNode(index).data;
    }

    /**
     * 设置指定位置的元素
     * 时间复杂度：O(n)
     * @param index 要设置的位置
     * @param element 新的元素
     * @return 原来的元素
     */
    public T set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("索引越界: " + index);
        }
        Node<T> current = getNode(index);
        T result = current.data;
        current.data = element;
        return result;
    }

    /**
     * 查找元素的索引位置
     * 时间复杂度：O(n)
     * @param element 要查找的元素
     * @return 元素索引，不存在则返回-1
     */
    public int indexOf(T element) {
        Node<T> current = head;
        int index = 0;
        while (current != null) {
            if (current.data.equals(element)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    /**
     * 判断是否包含指定元素
     * 时间复杂度：O(n)
     * @param element 要判断的元素
     * @return 是否包含
     */
    public boolean contains(T element) {
        return indexOf(element) != -1;
    }

    /**
     * 获取元素数量
     * @return 当前元素数量
     */
    public int size() {
        return size;
    }

    /**
     * 判断是否为空
     * @return 是否为空
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * 清空所有元素
     */
    public void clear() {
        head = tail = null;
        size = 0;
    }

    /**
     * 获取指定位置的节点
     * 优化：从离目标位置更近的一端开始遍历
     * @param index 节点位置
     * @return 指定位置的节点
     */
    private Node<T> getNode(int index) {
        Node<T> current;
        if (index < size / 2) {
            current = head;
            for (int i = 0; i < index; i++) {
                current = current.next;
            }
        } else {
            current = tail;
            for (int i = size - 1; i > index; i--) {
                current = current.prev;
            }
        }
        return current;
    }

    /**
     * 返回迭代器
     */
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private Node<T> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public T next() {
                T data = current.data;
                current = current.next;
                return data;
            }
        };
    }

    /**
     * 返回链表的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Node<T> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next;
        }
        sb.append("]");
        return sb.toString();
    }
}
