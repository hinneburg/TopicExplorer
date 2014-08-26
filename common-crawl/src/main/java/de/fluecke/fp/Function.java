package de.fluecke.fp;

import java.util.ArrayList;
import java.util.List;

public class Function {
    public static <In, Out> List<Out> map(Function1<In, Out> f, Iterable<In> l) {
        List<Out> newList = new ArrayList<Out>();
        for (In obj : l) {
            Out newObj = f.call(obj);
            newList.add(newObj);
        }
        return newList;
    }
    
    public static <In> List<In> filter(Function1<In, Boolean> f, Iterable<In> l) {
        List<In> newList = new ArrayList<In>();
        for (In in : l) {
            if (f.call(in) == true) {
                newList.add(in);
            }
        }
        return newList;
    }
    
    public static <In, Out> Out reduce(Function2<Out, In, Out> f, Out first, Iterable<In> l) {
        Out current = first;
        for (In in : l) {
            current = f.call(current, in);
        }
        
        return current;
    }
}
