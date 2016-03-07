package net.magik6k.bitbuffer;

import java.lang.reflect.Field;
import java.nio.BufferOverflowException;

import sun.misc.Cleaner;
import sun.misc.Unsafe;

class DirectBitBuffer extends SimpleBitBuffer{
	private static final Unsafe unsafe;
	
	private long address;
	private final long size;
	private final Deallocator dealloc;
	final Cleaner finalizer; //unused, because Cleaner's class strongly references all instances
	
	static {
		Field theUnsafe;
		Unsafe us = null;
		try{
			theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			us = (Unsafe) theUnsafe.get(null);
		}catch(Exception e){
			throw new Error(Unsafe.class.getName()+" is not accessible",e);
		}
		unsafe = us;
	}
	protected DirectBitBuffer(long bits){
		long bytes = (long) Math.ceil(bits / 8.d);
		try {
			address = unsafe.allocateMemory(bytes);
		} catch (OutOfMemoryError ex) {
            throw ex;
        }
		
		Deallocator dall = new Deallocator(address);
		dealloc = dall;
		finalizer = Cleaner.create(this,dall);//point finalizer to this object
		
		unsafe.setMemory(address,bytes,(byte)0);
		size = bytes;
	}
	
	protected void reallocate(long newBits){
		long oldsize = size;
		long newsize = (newBits+(8-newBits%8))/8;
		if(newsize > oldsize){
			long oldaddress = address;
			long newaddress = unsafe.reallocateMemory(oldaddress,newsize);
			dealloc.address = newaddress;
			address = newaddress;
			unsafe.setMemory(newaddress+oldsize,newsize-oldsize,(byte)0);
		}else if(newsize < oldsize){
			long newaddress = unsafe.reallocateMemory(address,newsize);
			dealloc.address = newaddress;
			address = newaddress;
		}
	}
	
	//@Override
	protected byte rawGet(long index) {
		if(index >= size)
			throw new BufferOverflowException();
		return unsafe.getByte(address + index);
	}

	//@Override
	protected void rawSet(long index, byte value) {
		if(index >= size)
			throw new BufferOverflowException();
		unsafe.putByte(address + index, value);
	}

	//@Override
	protected long rawLength(){
		return size;
	}
	static class Deallocator implements Runnable{
		volatile long address;
		public Deallocator(long adr){
			address = adr;
		}
		@Override
		public void run(){ //invoked when buffer already died
			long address = this.address;
			if(address == 0){
				//already done?!?
				return;
			}
			unsafe.freeMemory(address);
			this.address = 0;
		}
		
	}
}