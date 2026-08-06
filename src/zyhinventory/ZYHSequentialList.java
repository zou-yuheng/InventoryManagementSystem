package zyhinventory;

import java.util.Arrays;

/**
 * Sequential List (Array Implementation)
 * An array-based data structure that supports random access
 * Used to store product lists, inventory lists, etc. that require frequent random access
 * @param <T> Generic type
 */
public class ZYHSequentialList<T> {
    /** Array storing elements */
    private Object[] elements;

    /** Current number of elements */
    private int size;

    /** Default initial capacity */
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * Default constructor
     * Creates a sequential list with capacity 16
     */
    public ZYHSequentialList() {
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * Constructor
     * @param initialCapacity Initial capacity
     */
    public ZYHSequentialList(int initialCapacity) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity cannot be negative: " + initialCapacity);
        }
        elements = new Object[initialCapacity];
        size = 0;
    }

    /**
     * Add element at the end
     * Time complexity: O(1) amortized
     * @param element Element to add
     */
    public void add(T element) {
        if (size >= elements.length) {
            expandCapacity();  // Expand capacity when insufficient
        }
        elements[size++] = element;
    }

    /**
     * Insert element at specified position
     * Time complexity: O(n)
     * @param index Insert position
     * @param element Element to insert
     */
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        if (size >= elements.length) {
            expandCapacity();  // Expand capacity when insufficient
        }
        // Move elements at index and after backward
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }
        elements[index] = element;
        size++;
    }

    /**
     * Remove element at specified position
     * Time complexity: O(n)
     * @param index Position of element to remove
     * @return Removed element
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        @SuppressWarnings("unchecked")
        T result = (T) elements[index];
        // Move elements after index forward
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }
        elements[--size] = null;  // Release reference, help garbage collection
        return result;
    }

    /**
     * Remove specified element
     * Time complexity: O(n)
     * @param element Element to remove
     * @return Whether removal was successful
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
     * Get element at specified position
     * Time complexity: O(1)
     * @param index Element position
     * @return Element at specified position
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        @SuppressWarnings("unchecked")
        T result = (T) elements[index];
        return result;
    }

    /**
     * Set element at specified position
     * Time complexity: O(1)
     * @param index Position to set
     * @param element New element
     * @return Original element
     */
    public T set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        @SuppressWarnings("unchecked")
        T result = (T) elements[index];
        elements[index] = element;
        return result;
    }

    /**
     * Find index position of element
     * Time complexity: O(n)
     * @param element Element to find
     * @return Element index, returns -1 if not exists
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
     * Determine whether specified element is contained
     * Time complexity: O(n)
     * @param element Element to check
     * @return Whether contained
     */
    public boolean contains(T element) {
        return indexOf(element) != -1;
    }

    /**
     * Get number of elements
     * @return Current number of elements
     */
    public int size() {
        return size;
    }

    /**
     * Determine whether empty
     * @return Whether empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clear all elements
     */
    public void clear() {
        Arrays.fill(elements, 0, size, null);  // Release all element references
        size = 0;
    }

    /**
     * Convert to array
     * @return Array containing all elements
     */
    public Object[] toArray() {
        return Arrays.copyOf(elements, size);
    }

    /**
     * Ensure sufficient capacity
     * @param minCapacity Minimum required capacity
     */
    public void ensureCapacity(int minCapacity) {
        if (minCapacity > elements.length) {
            int newCapacity = Math.max(minCapacity, elements.length * 2);
            elements = Arrays.copyOf(elements, newCapacity);
        }
    }

    /**
     * Expansion method
     * Double the capacity
     */
    private void expandCapacity() {
        int newCapacity = elements.length * 2;
        elements = Arrays.copyOf(elements, newCapacity);
    }

    /**
     * Return string representation of the sequential list
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
