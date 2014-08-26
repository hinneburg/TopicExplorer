package de.fluecke.fp;

import de.fluecke.fp.Function1;

public abstract class Function2<I, I2, O> {
    public abstract O call(I i, I2 i2);
    
    public Function1<I2, O> partial(I i) {
        final I finalI = i;
        final Function2<I, I2, O> finalThis = this;
        Function1<I2, O> ret = new Function1<I2, O>() {
            @Override
            public O call(I2 i2) {
                return finalThis.call(finalI, i2);
            }
        };
        return ret;
    }
}
