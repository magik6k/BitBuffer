package net.magik6k.bitbuffer;

class ArrayBitBuffer extends SimpleBitBuffer{
	private byte[] bytes;
	
	protected ArrayBitBuffer(long bits) {
		bytes = new byte[(int) ((bits+(8L-bits%8L))/8L)];
		limit = bits;
	}
	
	protected ArrayBitBuffer(byte[] bytes) {
		this.bytes = bytes;
		limit = bytes.length * 8L;
	}

	@Override
	protected byte rawGet(long index) {
		return bytes[(int) index];
	}

	@Override
	protected void rawSet(long index, byte value) {
		bytes[(int) index] = value;
	}

	@Override
	protected long rawLength() {
		return bytes.length;
	}
}
