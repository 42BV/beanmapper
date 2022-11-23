package io.beanmapper.utils.provider;

public class ByteProvider implements Provider<Byte> {
    @Override
    public Byte getDefault() {
        return (byte) 0;
    }

    @Override
    public Byte getMaximum() {
        return Byte.MAX_VALUE;
    }
}
