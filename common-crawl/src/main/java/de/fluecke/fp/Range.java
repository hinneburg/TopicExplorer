package de.fluecke.fp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.fluecke.fp.RangeIterator;

public class Range implements Iterable<Integer> {
    private RangeIterator iter;

    public Range(Integer start, Integer stop, Integer step) {
        this.iter = new RangeIterator(start, stop, step);
    }
    
    public Range(Integer start, Integer stop) {
        this(start, stop, 1);
    }

    public Range(Integer stop) {
        this(0, stop, 1);
    }

    public Iterator<Integer> iterator() {
        return this.iter;
    }
    
    public List<Integer> toList() {
        List<Integer> newList = new ArrayList<Integer>();
        for (Integer integer : this) {
            newList.add(integer);
        }
        return newList;
    }
}
