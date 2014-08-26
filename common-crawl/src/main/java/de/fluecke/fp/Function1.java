package de.fluecke.fp;

public abstract class Function1<I, O> {
    public abstract O call(I in);
    
    public <I2> Function1<I2, O> compose(Function1<I2, I> f) {
        final Function1<I, O> finalThis = this;
        final Function1<I2, I> finalF = f;
        Function1<I2, O> ret = new Function1<I2, O>() {
            @Override
            public O call(I2 in) {
                I interm = finalF.call(in);
                return finalThis.call(interm);
            }
        };
        return ret;
    }
}
