package de.fluecke.fp.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fluecke.fp.Function1;
import de.fluecke.fp.Function2;

public class Function2Test {
    @Test
    public void testPartial_concat() {
        Function2<String, String, String> concat = new Function2<String, String, String>() {
            @Override
            public String call(String i, String i2) {
                return i + i2;
            }
        };
        
        Function1<String, String> helloFunc = concat.partial("Hello, ");
        Function1<String, String> byeFunc = concat.partial("Bye, ");
        assertEquals("Hello, World!", helloFunc.call("World!"));
        assertEquals("Bye, World!", byeFunc.call("World!"));
    }

    @Test
    public void testPartial_increment() {
        Function2<Integer, Integer, Integer> add = new Function2<Integer, Integer, Integer>() {
            @Override
            public Integer call(Integer i, Integer i2) {
                return i + i2;
            }
        };
        
        Function1<Integer, Integer> increment = add.partial(1);
        assertEquals(Integer.valueOf(2), increment.call(1));
        assertEquals(Integer.valueOf(3), increment.call(2));
        assertEquals(Integer.valueOf(4), increment.call(3));
        assertEquals(Integer.valueOf(5), increment.call(4));
        assertEquals(Integer.valueOf(6), increment.call(5));
    }
}
