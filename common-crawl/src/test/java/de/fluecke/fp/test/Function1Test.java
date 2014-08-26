package de.fluecke.fp.test;

import static org.junit.Assert.*;

import org.junit.Test;

import de.fluecke.fp.Function1;

public class Function1Test {
    @Test
    public void testCompose() {
        Function1<Integer, String> toString = new Function1<Integer, String>() {
            public String call(Integer in) {
                return "" + in;
            }
        };
        
        Function1<String, String> appendA = new Function1<String, String>() {
            public String call(String in) {
                return in + "a";
            }
        };
        
        Function1<Integer, String> numA = appendA.compose(toString);
        assertEquals(numA.call(1), appendA.call(toString.call(1)));
    }

}
