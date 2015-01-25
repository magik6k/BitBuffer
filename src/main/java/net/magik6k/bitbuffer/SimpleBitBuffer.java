package net.magik6k.bitbuffer;

abstract class SimpleBitBuffer extends BitBuffer{
	private boolean read = false;
	private long position;
	protected long limit;
	
	protected abstract byte rawGet(long index);
	protected abstract void rawSet(long index, byte value);
	protected abstract long rawLength();
	
	@Override
	public BitBuffer putBoolean(boolean b) {
		rawSet(position/8, (byte) ((rawGet(position/8) & ~(0x80 >>> (position % 8))) + ((b?0x80:0) >>> (position % 8))) );
		++position;
		return this;
	}

	@Override
	public BitBuffer putByte(byte b) {
		byte old = (byte) (rawGet(position/8) & (byte)~(0xFF >>> (position%8)));
		rawSet(position/8, (byte) (old | (byte)((b&0xFF) >>> (position % 8))));
		if(position % 8 > 0)
			rawSet((position/8)+1, (byte) ((b&0xFF) << (8-(position % 8))));
		position += 8;
		return this;
	}
	
	@Override
	public BitBuffer putByte(byte b, int bits) {
		b = (byte) (0xFF & ((b & (0xFF >>> (8 - bits))) << (8-bits)));
		rawSet(position/8, (byte) (0xFF & ((rawGet(position/8) & (0xFF << (8-position%8))) | ((b&0xFF) >>> (position%8)) )));
		if(8-(position % 8) < bits)
			rawSet((position/8)+1, (byte) (0xFF & ((b&0xFF) << (8-position % 8))));
		position += bits;
		return this;
	}

	@Override
	public boolean getBoolean() {
		boolean result = (rawGet(position/8) & (0x80 >>> (position % 8))) > 0 ;
		++position;
		return result;
	}

	@Override
	public byte getByte() {
		byte b = (byte) ((rawGet(position/8) & (0xFF >>> (position % 8))) << (position % 8));
		b = position % 8 > 0 ? (byte) (b | (((0xFF & rawGet((position/8)+1)) >>> (8-(position % 8))))) : b;
		position += 8;
		return b;
	}

	@Override
	public byte getByte(int bits) {
		boolean sign = (rawGet(position/8) & (0x80 >>> (position % 8))) > 0;
		position++;
		bits--;
		
		short mask = (short) (((0xFF00 << (8 - bits)) & 0xFFFF) >>> (position % 8));
		
		byte b = (byte) ((rawGet(position/8) & ((mask & 0xFF00) >>> 8)) << (position % 8));
		if(8-(position % 8) < bits)
			b = (byte) (b | ((0xFF & (rawGet((position/8)+1) & (mask & 0x00FF))) >>> (bits - ((position % 8) + bits - 8))));

		b = (byte) ((b&0xFF) >>> (8-bits));
		position += bits;
		
		return (byte) (sign ? ((0xFF << bits)&0xFF) | b : b);
	}
	
	@Override
	public byte getByteUnsigned(int bits) {
		short mask = (short) (((0xFF00 << (8 - bits)) & 0xFFFF) >>> (position % 8));
		
		byte b = (byte) ((rawGet(position/8) & ((mask & 0xFF00) >>> 8)) << (position % 8));
		if(8-(position % 8) < bits)
			b = (byte) (b | ((0xFF & (rawGet((position/8)+1) & (mask & 0x00FF))) >>> (bits - ((position % 8) + bits - 8))));

		b = (byte) ((b&0xFF) >>> (8-bits));
		position += bits;
		return b;
	}

	@Override
	public BitBuffer flip() {
		read = !read;
		if(read)
			limit = position;
		position = 0;
		return this;
	}

	@Override
	public boolean canRead() {
		return read;
	}

	@Override
	public boolean canWrite() {
		return !read;
	}

	@Override
	public long size() {
		return rawLength();
	}

	@Override
	public long limit() {
		return read ? limit : rawLength();
	}

	@Override
	public long position() {
		return position;
	}

	@Override
	public BitBuffer setPosition(long newPosition) {
		position = newPosition;
		return this;
	}
}
