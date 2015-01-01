package net.magik6k.bitbuffer;

import java.lang.reflect.Field;
import java.nio.BufferOverflowException;

import sun.misc.Unsafe;

class DirectBitBuffer extends SimpleBitBuffer{

	private final long address;
	private static Unsafe unsafe;
	private final long size;
	
	static {
		Field theUnsafe;
		try {
			theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			unsafe = (Unsafe) theUnsafe.get(null);
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	protected DirectBitBuffer(long bits){
		try {
			address = unsafe.allocateMemory((bits+(8-bits%8))/8);
		} catch (OutOfMemoryError ex) {
            throw ex;
        }
		unsafe.setMemory(address, (bits+(8-bits%8))/8, (byte) 0);
		size = (bits+(8-bits%8))/8;
	}
	
	@Override
	protected byte rawGet(long index) {
		if(index >= size)
			throw new BufferOverflowException();
		return unsafe.getByte(address + index);
	}

	@Override
	protected void rawSet(long index, byte value) {
		if(index >= size)
			throw new BufferOverflowException();
		unsafe.putByte(address + index, value);
	}

	@Override
	protected long rawLength() {
		return (int) size;
	}

	@Override
	protected void finalize() throws Throwable {
		unsafe.freeMemory(address);
		super.finalize();
	}
}
