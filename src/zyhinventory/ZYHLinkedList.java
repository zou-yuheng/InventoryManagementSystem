package zyhinventory;

import java.util.Iterator;

/**
 * Doubly Linked List Implementation
 * A node-connection based data structure that supports efficient insertion and deletion
 * Used to store inbound record queues, outbound record stacks, transaction records, etc.
 * that require frequent addition and deletion
 * @param <T> Generic type
 */
public class ZYHLinkedList<T> implements Iterable<T> {
    /** Head node */
    private Node<T> head;

    /** Tail node */
    private Node<T> tail;

    /** Current number of elements */
    private int size;

    /**
     * Node inner class
     * Contains data field and prev/next pointers
     */
    private static class Node<T> {
        /** Node data */
        T data;
        /** Points to next node */
        Node<T> next;
        /** Points to previous node */
        Node<T> prev;

        Node(T data) {
            this.data = data;
            this.next = null;
            this.prev = null;
        }
    }

    /**
     * Default constructor
     */
    public ZYHLinkedList() {
        head = null;
        tail = null;
        size = 0;
    }

    /**
     * Add element at the head of the list
     * Time complexity: O(1)
     * @param element Element to add
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
     * Add element at the tail of the list
     * Time complexity: O(1)
     * @param element Element to add
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
     * Insert element at specified position
     * Time complexity: O(n)
     * @param index Insert position
     * @param element Element to insert
     */
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
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
     * Remove head element
     * Time complexity: O(1)
     * @return Removed element
     */
    public T removeFirst() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("Linked list is empty");
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
     * Remove tail element
     * Time complexity: O(1)
     * @return Removed element
     */
    public T removeLast() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("Linked list is empty");
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
     * Remove element at specified position
     * Time complexity: O(n)
     * @param index Element position
     * @return Removed element
     */
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
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
     * Remove specified element
     * Time complexity: O(n)
     * @param element Element to remove
     * @return Whether removal was successful
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
     * Get head element
     * Time complexity: O(1)
     * @return Head element
     */
    public T getFirst() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("Linked list is empty");
        }
        return head.data;
    }

    /**
     * Get tail element
     * Time complexity: O(1)
     * @return Tail element
     */
    public T getLast() {
        if (isEmpty()) {
            throw new IndexOutOfBoundsException("Linked list is empty");
        }
        return tail.data;
    }

    /**
     * Get element at specified position
     * Time complexity: O(n)
     * @param index Element position
     * @return Element at specified position
     */
    public T get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        return getNode(index).data;
    }

    /**
     * Set element at specified position
     * Time complexity: O(n)
     * @param index Position to set
     * @param element New element
     * @return Original element
     */
    public T set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index out of bounds: " + index);
        }
        Node<T> current = getNode(index);
        T result = current.data;
        current.data = element;
        return result;
    }

    /**
     * Find index position of element
     * Time complexity: O(n)
     * @param element Element to find
     * @return Element index, returns -1 if not exists
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
        head = tail = null;
        size = 0;
    }

    /**
     * Get node at specified position
     * Optimization: traverse from the end closer to target position
     * @param index Node position
     * @return Node at specified position
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
     * Return iterator
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
     * Return string representation of the linked list
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
