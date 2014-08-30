package de.fluecke.fp.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import de.fluecke.fp.Range;

public class RangeTest {
    @Test
    public void testConstructor1() {
        Integer start = 125;
        Integer stop = 993;
        Integer step = 58;
        Range range = new Range(start, stop, step);
        assertRange(range, start, stop, step);
    }
    
    @Test
    public void testConstructor2() {
        Integer start = 450;
        Integer stop = 1000;
        Range range = new Range(start, stop);
        assertRange(range, start, stop, 1);
    }
    
    @Test
    public void testConstructor3() {
        Integer stop = 345;
        Range range = new Range(stop);
        assertRange(range, 0, stop, 1);
    }
    
    protected static void assertRange(Iterable<Integer> r, Integer start, Integer stop, Integer step) {
        Integer current = start;
        for (Integer integer : r) {
            assertEquals(current + " should equal " + integer, current, integer);
            current += step;
        }
        
        Integer last = current - step;
        
        assertTrue(last + " should be be smaller than " + stop, last < stop);
    }
    
    @Test
    public void testToListConstructor1() {
        Integer start = 23;
        Integer stop = 392;
        Integer step = 8;
        Range range = new Range(start, stop, step);
        assertRange(range.toList(), start, stop, step);
    }

    @Test
    public void testToListConstructor2() {
        Integer start = 294;
        Integer stop = 932;
        Range range = new Range(start, stop);
        assertRange(range.toList(), start, stop, 1);
    }

    @Test
    public void testToListConstructor3() {
        Integer stop = 271;
        Range range = new Range(stop);
        assertRange(range.toList(), 0, stop, 1);
    }
}
