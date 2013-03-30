import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A randomized queue is similar to a stack or queue, except that the item removed is chosen uniformly
 * at random from items in the data structure.
 * <p/>
 * The implementation supports each randomized queue operation (besides creating an iterator)
 * in constant amortized time and uses space proportional to the number of items currently in the queue.
 * That is, any sequence of M randomized queue operations (starting from an empty queue) should take at most cM steps
 * in the worst case, for some constant c. Additionally, the iterator implementation supports construction in time
 * linear in the number of items and it supports the operations next() and hasNext() in constant worst-case time;
 * The order of two or more iterators to the same randomized queue should be mutually independent;
 * each iterator must maintain its own random order.
 * <p/>
 * NOTES:
 * <p/>
 * The implementation is based on ResizingArrayStack internals.
 */
public class RandomizedQueue<Item> implements Iterable<Item> {

    /**
     * Queue elements.
     */
    private Item[] items;

    /**
     * Number of elements on queue.
     */
    private int size = 0;

    /**
     * Construct an empty randomized queue.
     */
    public RandomizedQueue() {
        items = (Item[]) new Object[1];
    }

    /**
     * Is the queue empty?
     *
     * @return whether the queue is empty or not.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * @return the number of items on the queue.
     */
    public int size() {
        return size;
    }

    /**
     * Ensures that an item reference passed as a parameter to the calling method is not null.
     *
     * @param item an item reference.
     * @return the non-null reference that was validated.
     * @throws NullPointerException if reference is null.
     */
    private Item checkItemNotNull(Item item) throws NullPointerException {
        if (item == null) {
            throw new NullPointerException("The item must not be null");
        }
        return item;
    }

    /**
     * @throws NoSuchElementException if this queue is empty.
     */
    private void checkQueueIsNotEmpty() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException("RandomizedQueue underflow");
        }
    }

    // resize the underlying array
    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < size; i++) {
            copy[i] = items[i];
        }
        items = copy;
    }

    /**
     * Add the item.
     *
     * @param item the item to add.
     * @throws NullPointerException if the client attempts to add a null item.
     */
    public void enqueue(Item item) {
        checkItemNotNull(item);

        // double size of array if necessary and recopy to front of array
        if (size == items.length) {
            // double size of array if necessary
            resize(2 * items.length);
        }
        // add item
        items[size++] = item;
    }

    /**
     * Delete and return a random item.
     *
     * @return the deleted item.
     * @throws NoSuchElementException if the client attempts to remove an item from an empty queue.
     */
    public Item dequeue() throws NoSuchElementException {
        checkQueueIsNotEmpty();

        // using random number generator, get index m
        int index = StdRandom.uniform(size);
        // get item with index m
        Item item = items[index];

        if (size - 1 != index) {
            // move last item in the array to index m
            items[index] = items[size - 1];
        }
        // set item with index m to null to avoid loitering
        items[size - 1] = null;

        size--;
        // shrink size of array if necessary
        if (size > 0 && size == items.length / 4) {
            resize(items.length / 2);
        }
        return item;
    }

    /**
     * @return (but do not delete) a random item.
     * @throws NoSuchElementException if the client attempts to fetch an item from an empty queue.
     */
    public Item sample() throws NoSuchElementException {
       checkQueueIsNotEmpty();

        int index = StdRandom.uniform(size);
        return items[index];
    }

    /**
     * @return an independent iterator over items in random order.
     */
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }

    // an iterator, doesn't implement remove() since it's optional
    private class RandomizedQueueIterator implements Iterator<Item> {

        private int currentPos = 0;

        private int[] shuffledIndices;

        private RandomizedQueueIterator() {
            shuffledIndices = new int[size];
            for (int i = 0; i < shuffledIndices.length; i++) {
                shuffledIndices[i] = i;
            }
            // rearrange the entities in random order using the Knuth algorithms
            // running in linear time
            StdRandom.shuffle(shuffledIndices);
        }

        @Override
        public boolean hasNext() {
            return currentPos < size;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            int index = shuffledIndices[currentPos++];
            return items[index];
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Method is not supported");
        }

    }

}
