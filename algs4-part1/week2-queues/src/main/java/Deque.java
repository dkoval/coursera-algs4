import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A double-ended queue or deque (pronounced "deck") is a generalization of a stack and a queue
 * that supports inserting and removing items from either the front or the back of the data structure.
 * <p/>
 * The implementation supports each deque operation in constant worst-case time and uses space proportional to
 * the number of items currently in the deque. Additionally, the iterator implementation supports the operations
 * next() and hasNext() (plus construction) in constant worst-case time and uses a constant amount of
 * extra space per iterator.
 */
public class Deque<Item> implements Iterable<Item> {

    /**
     * Beginning of deque.
     */
    private Node first;

    /**
     * End of deque.
     */
    private Node last;

    /**
     * Number of elements on deque.
     */
    private int size = 0;

    // helper doubly-linked list class; access modifiers do not matter
    private class Node {

        /**
         * Data field.
         */
        private Item item;

        /**
         * Link to the previous node in the sequence.
         */
        private Node prev;

        /**
         * Link to the next node in the sequence.
         */
        private Node next;

        private Node(Item item) {
            this.item = item;
        }

    }

    /**
     * Construct an empty deque.
     */
    public Deque() {
        first = null;
        last = null;
    }

    /**
     * Is the deque empty?
     *
     * @return whether the deque is empty or not.
     */
    public boolean isEmpty() {
        return (size == 0);
    }

    /**
     * @return the number of items on the deque.
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
     * @throws NoSuchElementException if this deque is empty.
     */
    private void checkDequeIsNotEmpty() throws NoSuchElementException {
        if (isEmpty()) {
            throw new NoSuchElementException("Deque underflow");
        }
    }

    /**
     * Insert the item at the front.
     *
     * @param item the item to insert.
     * @throws NullPointerException if the client attempts to add a null item.
     */
    public void addFirst(Item item) throws NullPointerException {
        checkItemNotNull(item);

        Node oldfirst = first;
        first = new Node(item);

        if (isEmpty()) {
            last = first;
        } else {
            oldfirst.prev = first;
            first.next = oldfirst;
        }
        size++;
    }

    /**
     * Insert the item at the end.
     *
     * @param item the item to insert.
     * @throws NullPointerException if the client attempts to add a null item.
     */
    public void addLast(Item item) throws NullPointerException {
        checkItemNotNull(item);

        Node oldlast = last;
        last = new Node(item);

        if (isEmpty()) {
            first = last;
        } else {
            oldlast.next = last;
            last.prev = oldlast;
        }
        size++;
    }

    /**
     * Delete and return the item at the front.
     *
     * @return the first item on the deque.
     * @throws NoSuchElementException if the client attempts to remove an item from an empty deque.
     */
    public Item removeFirst() throws NoSuchElementException {
        checkDequeIsNotEmpty();

        Item item = first.item;
        first = first.next;
        size--;

        if (isEmpty()) {
            // to avoid loitering; first already points to null
            last = null;
        } else {
            first.prev = null;
        }
        return item;
    }

    /**
     * Delete and return the item at the end.
     *
     * @return the last item on the queue.
     * @throws NoSuchElementException if the client attempts to remove an item from an empty deque.
     */
    public Item removeLast() throws NoSuchElementException {
        checkDequeIsNotEmpty();

        Item item = last.item;
        last = last.prev;
        size--;

        if (isEmpty()) {
            // to avoid loitering; last already points to null
            first = null;
        } else {
            last.next = null;
        }
        return item;
    }

    /**
     * @return an iterator over items in order from front to end.
     */
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    // an iterator, doesn't implement remove() since it is optional
    private class DequeIterator implements Iterator<Item> {

        private Node current = first;

        @Override
        public boolean hasNext() {
            return (current != null);
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Item item = current.item;
            current = current.next;
            return item;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Method is not supported");
        }

    }

}