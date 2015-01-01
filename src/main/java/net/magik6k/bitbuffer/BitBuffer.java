package net.magik6k.bitbuffer;

import java.nio.ByteBuffer;

import com.google.common.base.Charsets;

public abstract class BitBuffer {
	/**
	 * Puts boolean value(Single bit)
	 * @param b value to set
	 * @return This buffer
	 */
	public abstract BitBuffer putBoolean(boolean b);
	
	/**
	 * Puts byte value(8 bits)
	 * @param b value to set
	 * @return This buffer
	 */
	public abstract BitBuffer putByte(byte b);
	
	/**
	 * Puts byte value with specified bit count. Note that this
	 * method can only be used with positive numbers or zero.
	 * @param b value to set
	 * @param bits Number of bits to use
	 * @return This buffer
	 */
	public abstract BitBuffer putByte(byte b, int bits);
	
	/**
	 * Puts integer value(32 bits)
	 * @param i value to set
	 * @return This buffer
	 */
	public BitBuffer putInt(int i){
		putByte((byte)((i&0xFF000000)>>>24));
		putByte((byte)((i&0x00FF0000)>>>16));
		putByte((byte)((i&0x0000FF00)>>>8));
		putByte((byte) (i&0x000000FF));
		return this;
	}
	
	/**
	 * Puts integer value with specified bit count. Note that this
	 * method can only be used with positive numbers or zero.
	 * @param i value to set
	 * @param bits Number of bits to use
	 * @return This buffer
	 */
	public BitBuffer putInt(int i, int bits){
		if(bits == 0)return this;
		do{
			if(bits > 7){
				putByte((byte) ((i&(0xFF << (bits - 8))) >>> (bits - 8) ));
				bits -= 8;
			}else{
				putByte((byte) (i & (0xFF >> -(bits - 8))), bits);
				bits = 0;
			}
		}while(bits > 0);
		return this;
	}
	
	/**
	 * Puts floating point value(32 bits)
	 * @param f value to set
	 * @return This buffer
	 */
	public BitBuffer putFloat(float f){
		int asInt = Float.floatToRawIntBits(f);
		putInt(asInt);
		return this;
	}
	
	/**
	 * Puts {@link String} value(8 bits per char), using UTF-8
	 * @param s value to set
	 * @return This buffer
	 */
	public BitBuffer putString(String s){
		for(byte ch : s.getBytes(Charsets.UTF_8)){
			putByte(ch);
		}
		return this;
	}
	
	/**
	 * Puts {@link String} value(specified amount bits per char), using UTF-8
	 * @param s value to set
	 * @param bitsPerChar amount of bits to use per character
	 * @return This buffer
	 */
	public BitBuffer putString(String s, int bitsPerChar){
		for(byte ch : s.getBytes(Charsets.UTF_8)){
			putByte(ch, bitsPerChar);
		}
		return this;
	}
	
	/**
	 * @return Binary value of current bit
	 */
	public abstract boolean getBoolean();
	
	/**
	 * @return 8 bit byte value
	 */
	public abstract byte getByte();
	
	/**
	 * @param bits length of value in bits
	 * @return Byte value of given bit width
	 */
	public abstract byte getByte(int bits);
	
	/**
	 * @return 32 bit integer value
	 */
	public int getInt(){
		return ((getByte()&0xFF) << 24) | ((getByte()&0xFF) << 16) | ((getByte()&0xFF) << 8) | (getByte()&0xFF);
	}
	
	/**
	 * @param bits Length of integer
	 * @return Integer value of given bit width
	 */
	public int getInt(int bits){
		if(bits == 0)return 0;
		int res = 0;
		do {
			if(bits > 7){
				res = (res << 8) | (getByte()&0xFF);
				bits -= 8;
			}else{
				res = (res << bits) + (getByte(bits)&0xFF);
				bits -= bits;
			}
		}while(bits > 0);
		return res;
	}
	
	/**
	 * @return 32 bit floating point value
	 */
	public float getFloat(){
		return Float.intBitsToFloat(getInt());
	}
	
	/**
	 * @param length Length of the string
	 * @return String of given length, with 8-bit wide characters
	 */
	public String getString(int length){
		byte[] bytes = new byte[length];
		for(int i = 0; i < length; ++i){
			bytes[i] = getByte();
		}
		return new String(bytes, Charsets.UTF_8);
	}
	
	/**
	 * @param length Length of the string
	 * @param bitsPerChar amount of bits used per char
	 * @return String of given length
	 */
	public String getString(int length, int bitsPerChar){
		byte[] bytes = new byte[length];
		for(int i = 0; i < length; ++i){
			bytes[i] = getByte(bitsPerChar);
		}
		return new String(bytes, Charsets.UTF_8);
	}
	
	/**
	 * Toggles the buffer betwen read/write modes. 
	 * Limit is set at current position, and cursor is set at position 0
	 */
	public abstract void flip();
	
	/**
	 * @return Boolean value telling if the buffer can output data
	 */
	public abstract boolean canRead();
	
	/**
	 * @return Boolean value telling if the buffer can accept data
	 */
	public abstract boolean canWrite();
	
	/**
	 * This method returns representation of this bufer as
	 * array of bytes. nota that not-full bits will be set to 0.
	 * This method shouldn't affect the position.
	 * @return This BitBuffer represented as byte array
	 */
	public byte[] asByteArray(){
		if(!canRead())
			throw new IllegalStateException("BitBuffer cannot be read");
		byte[] result = new byte[limit()];
		int startPos = position();
		setPosition(0);
		for(int i = 0; i < limit(); ++i){
			result[i] = getByte();
		}
		setPosition(startPos);
		return result;
	}
	
	/**
	 * This method returns representation of this bufer as
	 * ByteBuffer. nota that not-full bits will be set to 0.
	 * This method shouldn't affect the position.
	 * @return ByteBuffer version of this class
	 */
	public ByteBuffer asByteBuffer(){
		return ByteBuffer.wrap(asByteArray());
	}
	
	/**
	 * Puts this BitBuffer into ByteBuffer
	 * @param bb ByteBuffer to put data to
	 * @return This buffer
	 */
	public BitBuffer putToByteBuffer(ByteBuffer bb){
		bb.put(asByteArray());
		return this;
	}
	
	/**
	 * @return Size of this buffer, in bits
	 */
	public abstract int size();
	
	/**
	 * @return Virtual 'end' of this buffer, in bits
	 */
	public abstract int limit();
	
	/**
	 * @return Current position of cursor, in bits
	 */
	public abstract int position();
	
	/**
	 * Sets cursor position for this buffer
	 * @return This buffer
	 */
	public abstract BitBuffer setPosition(int newPosition);
	
	public static BitBuffer allocate(int bits){
		return new SimpleBitBuffer(bits);
	}
	
	protected static class SimpleBitBuffer extends BitBuffer{
		private byte[] bytes;
		private boolean read = false;
		private int position;
		private int limit;
		
		protected SimpleBitBuffer(int bits) {
			bytes = new byte[bits/8];
			limit = bits;
		}
		
		protected SimpleBitBuffer(byte[] bytes) {
			this.bytes = bytes;
		}
		
		@Override
		public BitBuffer putBoolean(boolean b) {
			bytes[position/8] = (byte) ((bytes[position/8] & ~(0x80 >>> (position % 8))) + ((b?0x80:0) >>> (position % 8)) );
			++position;
			return this;
		}

		@Override
		public BitBuffer putByte(byte b) {
			byte old = (byte) (bytes[position/8] & (byte)~(0xFF >>> (position%8)));
			bytes[position/8] = (byte) (old | (byte)((b&0xFF) >>> (position % 8)));
			if(position % 8 > 0)
				bytes[(position/8) + 1] = (byte) ((b&0xFF) << (8-(position % 8)));
			position += 8;
			return this;
		}
		
		@Override
		public BitBuffer putByte(byte b, int bits) {
			b = (byte) (0xFF & ((b & (0xFF >>> (8 - bits))) << (8-bits)));
			bytes[position/8] = (byte) (0xFF & ((bytes[position/8] & (0xFF << (8-position%8))) | ((b&0xFF) >>> (position%8)) ));
			if(8-(position % 8) < bits)
				bytes[(position/8) + 1] = (byte) (0xFF & ((b&0xFF) << (8-position % 8)));
			position += bits;
			return this;
		}

		@Override
		public boolean getBoolean() {
			boolean result = (bytes[position/8] & (0x80 >>> (position % 8))) > 0 ;
			++position;
			return result;
		}

		@Override
		public byte getByte() {
			byte b = (byte) ((bytes[position/8] & (0xFF >>> (position % 8))) << (position % 8));
			b = position % 8 > 0 ? (byte) (b | (((0xFF & bytes[(position/8)+1]) >>> (8-(position % 8))))) : b;
			position += 8;
			return b;
		}

		@Override
		public byte getByte(int bits) {
			short mask = (short) (((0xFF00 << (8 - bits)) & 0xFFFF) >>> (position % 8));
			
			byte b = (byte) ((bytes[position/8] & ((mask & 0xFF00) >>> 8)) << (position % 8));
			if(8-(position % 8) < bits)
				b = (byte) (b | ((0xFF & (bytes[(position/8)+1] & (mask & 0x00FF))) >>> (bits - ((position % 8) + bits - 8))));

			b = (byte) ((b&0xFF) >>> (8-bits));
			position += bits;
			return b;
		}

		@Override
		public void flip() {
			read = !read;
			position = 0;
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
		public int size() {
			return bytes.length;
		}

		@Override
		public int limit() {
			return read ? limit : bytes.length;
		}

		@Override
		public int position() {
			return position;
		}

		@Override
		public BitBuffer setPosition(int newPosition) {
			position = newPosition;
			return this;
		}
		
		@Override
		public byte[] asByteArray(){
			return bytes;//FIXME: is this unsafe?
		}
		
	}
}
