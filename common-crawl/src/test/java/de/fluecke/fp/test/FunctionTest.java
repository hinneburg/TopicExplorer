package de.fluecke.fp.test;

import static de.fluecke.fp.Function.*;

import java.util.List;

import org.junit.Test;

import de.fluecke.fp.Function1;
import de.fluecke.fp.Function2;
import de.fluecke.fp.Range;
import static de.fluecke.fp.test.RangeTest.assertRange;
import static org.junit.Assert.*;

public class FunctionTest {
    @Test
    public void testMap() {
        Function1<Integer, Integer> plusOne = new Function1<Integer, Integer>() {
            @Override
            public Integer call(Integer in) {
                return in + 1;
            }
        };
        
        Range r = new Range(5000);
        List<Integer> res = map(plusOne, r);
        assertRange(res, 1, 5001, 1);
    }

    @Test
    public void testFilter() {
        Function1<Integer, Boolean> even = new Function1<Integer, Boolean>() {
            @Override
            public Boolean call(Integer in) {
                return in % 2 == 0;
            }
        };
        
        Range r = new Range(5000);
        List<Integer> res = filter(even, r);
        assertRange(res, 0, 5000, 2);
    }

    @Test
    public void testReduce() {
        Function2<Integer, Integer, Integer> add = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer i, Integer i2) {
                return i + i2;
            }
        };
        
        Range r = new Range(5001);
        Integer sum = reduce(add, 0, r);
        Integer expectedSum = (5000 * (5000 + 1)) / 2;
        assertEquals(Integer.valueOf(sum), expectedSum);
    }
}
