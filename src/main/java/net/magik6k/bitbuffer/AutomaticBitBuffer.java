package net.magik6k.bitbuffer;


class AutomaticBitBuffer extends SimpleBitBuffer{
	private static final int DEFAULT_CAPACITY = 128;
	
	private byte[] bytes;
	
	protected AutomaticBitBuffer() {
		bytes = new byte[DEFAULT_CAPACITY];
	}
	
	protected AutomaticBitBuffer(long initialCapacity){
		bytes = new byte[(int)toBytes(initialCapacity)];
	}
	
	private static long toBytes(long bits){
		return (int) Math.ceil(bits / 8.d);
	}
	
	
	@Override
	protected byte rawGet(long index) {
		if(index >= bytes.length){
			ensureCapacity((int)index+1);
		}
		return bytes[(int)index];
	}
	
	private void ensureCapacity(int toBytes){
		byte[] newBytes = new byte[toBytes];
		System.arraycopy(bytes,0,newBytes,0,bytes.length);
		bytes = newBytes;
	}

	@Override
	protected void rawSet(long index, byte value) {
		if(index >= bytes.length){
			ensureCapacity((int)index+1);
		}
		bytes[(int)index] = value;
	}

	@Override
	protected long rawLength() {
		return bytes.length;
	}
	
}
