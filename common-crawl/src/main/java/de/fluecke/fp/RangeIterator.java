package de.fluecke.fp;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class RangeIterator implements Iterator<Integer> {
    private Integer stop;
    private Integer step;
    private Integer current;
    private Integer next;

    public RangeIterator(Integer start, Integer stop, Integer step) {
        this.current = start;
        this.next = start;
        this.stop = stop;
        this.step = step;
    }

    public Integer next() {
        current = next;
        next = current + step;
        if (current < stop) {
            return current;
        }
        throw new NoSuchElementException();
    }

    public boolean hasNext() {
        return current + step < stop;
    }

    public void remove() {
    }
}
