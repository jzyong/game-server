package com.jzy.game.ai.unity.nav.path;

import java.io.Serializable;

/**
 * A binary minheap of comparable objects.
 *
 *
 * @author Donald Chinn
 * @author Keith Woodward
 * @param <E>
 */
public class BinaryHeap<E extends Comparable<E>> implements Serializable {

	private static final long serialVersionUID = 1L;
	/* The heap is organized using the implicit array implementation.
     * Array index 0 is not used
     */
    public E[] elements;
    public int size;       // index of last element in the heap

    public BinaryHeap() {
        this(10);
    }

    /**
     * Constructor
     *
     * @param capacity number of active elements the heap can contain
     */
    @SuppressWarnings("unchecked")
	public BinaryHeap(int capacity) {
        this.elements = (E[]) new Comparable[capacity + 1];
        this.elements[0] = null;
        this.size = 0;
    }

    /**
     * Given an array of Comparables, return a binary heap of those elements.
     *
     * @param data an array of data (no particular order)
     * @return a binary heap of the given data
     */
    public static BinaryHeap buildHeap(Comparable[] data) {
        BinaryHeap newHeap = new BinaryHeap(data.length);
        System.arraycopy(data, 0, newHeap.elements, 1, data.length);
        newHeap.size = data.length;
        for (int i = newHeap.size / 2; i > 0; i--) {
            newHeap.percolateDown(i);
        }
        return newHeap;
    }

    /**
     * Determine whether the heap is empty.
     *
     * @return true if the heap is empty; false otherwise
     */
    public boolean isEmpty() {
        return (size < 1);
    }

    protected void doubleCapacityIfFull() {
        if (size >= elements.length - 1) {
            // not enough room -- create a new array and copy
            // the elements of the old array to the new
            E[] newElements = (E[]) new Comparable[2 * size];
            System.arraycopy(elements, 0, newElements, 0, elements.length);
            elements = newElements;
        }
    }

    /**
     * Insert an object into the heap.
     *
     * @param key a key
     */
    public void add(E key) {
        doubleCapacityIfFull();
        size++;
        elements[size] = key;
        percolateUp(size);
    }

    /**
     * Remove the object with minimum key from the heap.
     *
     * @return the object with minimum key of the heap
     */
    public E deleteMin() throws ArrayIndexOutOfBoundsException {
        if (!isEmpty()) {
            E returnValue = elements[1];
            elements[1] = elements[size];
            size--;
            percolateDown(1);
            return returnValue;

        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    public E peekMin() {
        if (!isEmpty()) {
            return elements[1];
        } else {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Given an index in the heap array, percolate that key up the heap.
     *
     * @param index an index into the heap array
     */
    public void percolateUp(int index) {
        E temp = elements[index];  // keep track of the item to be moved
        while (index > 1) {
            if (temp.compareTo(elements[index / 2]) < 0) {
                elements[index] = elements[index / 2];
                index = index / 2;
            } else {
                break;
            }
        }
        elements[index] = temp;
    }

    /**
     * Given an index in the heap array, percolate that key down the heap.
     *
     * @param index an index into the heap array
     */
    public void percolateDown(int index) {
        int child;
        E temp = elements[index];

        while (2 * index <= size) {
            child = 2 * index;
            if ((child != size)
                    && (elements[child + 1].compareTo(elements[child]) < 0)) {
                child++;
            }
            // ASSERT: at this point, elements[child] is the smaller of
            // the two children
            if (elements[child].compareTo(temp) < 0) {
                elements[index] = elements[child];
                index = child;
            } else {
                break;
            }
        }
        elements[index] = temp;

    }

    public void makeEmpty() {
        size = 0;
    }

    public boolean contains(Object x) {
        for (int i = 1; i <= size; i++) {
            if (elements[i] == x) {
                return true;
            }
        }
        return false;
    }

    public int indexOf(Object x) {
        for (int i = 1; i <= size; i++) {
            if (elements[i] == x) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Returns the logical size of the heap. The actual size of the underlying
     * array will be at least as big.
     *
     * @return the size of the heap
     */
    public int size() {
        return size;
    }

}
