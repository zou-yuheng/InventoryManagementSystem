package zyhinventory;

import java.util.Arrays;

/**
 * 顺序表（数组实现）
 * 一种基于数组的数据结构，支持随机访问
 * 用于存储商品列表、库存列表等需要频繁随机访问的数据
 * @param <T> 泛型类型
 */
public class ZYHSequentialList<T> {
    /** 存储元素的数组 */
    private Object[] elements;

    /** 当前元素数量 */
    private int size;

    /** 默认初始容量 */
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * 默认构造函数
     * 创建容量为16的顺序表
     */
    public ZYHSequentialList() {
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * 构造函数
     * @param initialCapacity 初始容量
     */
    public ZYHSequentialList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("初始容量不能为负数: " + initialCapacity);
        }
        elements = new Object[initialCapacity];
        size = 0;
    }

    /**
     * 在末尾添加元素
     * 时间复杂度：O(1)（均摊）
     * @param element 要添加的元素
     */
    public void add(T element) {
        if (size >= elements.length) {
            expandCapacity();  // 容量不足时扩容
        }
        elements[size++] = element;
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
        if (size >= elements.length) {
            expandCapacity();  // 容量不足时扩容
        }
        // 将index及其之后的元素向后移动
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
        elements[index] = element;
        size++;
    }

    /**
     * 移除指定位置的元素
     * 时间复杂度：O(n)
     * @param index 要移除元素的位置
     * @return 被移除的元素
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("索引越界: " + index);
        }
        @SuppressWarnings("unchecked")
        T result = (T) elements[index];
        // 将index之后的元素向前移动
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[--size] = null;  // 释放引用，帮助垃圾回收
        return result;
    }

    /**
     * 移除指定元素
     * 时间复杂度：O(n)
     * @param element 要移除的元素
     * @return 是否移除成功
     */
    public boolean remove(T element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取指定位置的元素
     * 时间复杂度：O(1)
     * @param index 元素位置
     * @return 指定位置的元素
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("索引越界: " + index);
        }
        @SuppressWarnings("unchecked")
        T result = (T) elements[index];
        return result;
    }

    /**
     * 设置指定位置的元素
     * 时间复杂度：O(1)
     * @param index 要设置的位置
     * @param element 新的元素
     * @return 原来的元素
     */
    public T set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("索引越界: " + index);
        }
        @SuppressWarnings("unchecked")
        T result = (T) elements[index];
        elements[index] = element;
        return result;
    }

    /**
     * 查找元素的索引位置
     * 时间复杂度：O(n)
     * @param element 要查找的元素
     * @return 元素索引，不存在则返回-1
     */
    public int indexOf(T element) {
        for (int i = 0; i < size; i++) {
            if (elements[i].equals(element)) {
                return i;
            }
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
        Arrays.fill(elements, 0, size, null);  // 释放所有元素引用
        size = 0;
    }

    /**
     * 转换为数组
     * @return 包含所有元素的数组
     */
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    /**
     * 确保容量足够
     * @param minCapacity 最小所需容量
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = Math.max(minCapacity, elements.length * 2);
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    /**
     * 扩容方法
     * 将容量扩大为原来的2倍
     */
    private void expandCapacity() {
        int newCapacity = elements.length * 2;
        elements = Arrays.copyOf(elements, newCapacity);
    }

    /**
     * 返回顺序表的字符串表示
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}
