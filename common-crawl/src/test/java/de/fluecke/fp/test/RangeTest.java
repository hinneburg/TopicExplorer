package de.fluecke.fp.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fluecke.fp.Range;

public class RangeTest {
    @Test
    public void testConstructor1() {
        Integer start = 12345;
        Integer stop = 99134156;
        Integer step = 5830;
        Range range = new Range(start, stop, step);
        assertRange(range, start, stop, step);
    }
    
    @Test
    public void testConstruvctor2() {
        Integer start = 450;
        Integer stop = 1000000;
        Range range = new Range(start, stop);
        assertRange(range, start, stop, 1);
    }
    
    @Test
    public void testConstruvctor3() {
        Integer stop = 345096;
        Range range = new Range(stop);
        assertRange(range, 0, stop, 1);
    }
    
    protected static void assertRange(Iterable<Integer> r, Integer start, Integer stop, Integer step) {
        Integer current = start;
        for (Integer integer : r) {
            assertEquals(current, integer);
            current += step;
        }
        
        assert(current < stop);
    }
    
    @Test
    public void testToListConstructor1() {
        Integer start = 2359;
        Integer stop = 3926809;
        Integer step = 8;
        Range range = new Range(start, stop, step);
        assertRange(range.toList(), start, stop, step);
    }

    @Test
    public void testToListConstructor2() {
        Integer start = 29416;
        Integer stop = 93260;
        Range range = new Range(start, stop);
        assertRange(range.toList(), start, stop, 1);
    }

    @Test
    public void testToListConstructor3() {
        Integer stop = 27159;
        Range range = new Range(stop);
        assertRange(range.toList(), 0, stop, 1);
    }
}
